package com.community.service;

import com.community.constant.BoardType;
import com.community.dto.PostFormDto;
import com.community.dto.PostMediaDto;
import com.community.entity.Member;
import com.community.entity.Post;
import com.community.entity.PostMedia;
import com.community.repository.MemberRepository;
import com.community.repository.PostMediaRepository;
import com.community.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMediaService postMediaService;
    private final PostMediaRepository postMediaRepository;
    private final MemberRepository memberRepository;

    // 페이지네이션 + 글목록
    public Page<Post> listPosts(BoardType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return postRepository.findByBoardTypeAndDeletedFalse(type, pageable);
    }

    // 글목록 N개씩만 조회
    public List<Post> listTopN(BoardType type, int n) {
        // Pageable로도 가능
        Pageable pageable = PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "id"));
        return postRepository.findByBoardTypeAndDeletedFalse(type, pageable).getContent();
    }

    // 게시글 저장
    @Transactional
    public Long savePost(BoardType boardType, PostFormDto postFormDto, List<MultipartFile> postMediaFileList) throws Exception {

        // 게시글 등록
        Post post = postFormDto.createPost();
        post.setBoardType(boardType);

        // 작성자 IP 부재시 기본값 채우기
        if (post.getIpAddress() == null || post.getIpAddress().isBlank()) {
            post.setIpAddress("0.0.0.0");
        }

        postRepository.save(post);

        // 미디어 등록
        if (postMediaFileList != null) {
            for (MultipartFile file : postMediaFileList) {
                if (file == null || file.isEmpty())
                    continue;

                PostMedia postMedia = new PostMedia();
                postMedia.setPost(post);
                postMediaService.savePostMedia(postMedia, file);
            }
        }
        return post.getId();
    }


    // 게시글 수정 위해 등록된 게시글 불러오기
    @Transactional(readOnly = true)
    public PostFormDto getPostDtl(Long postId) {

        List<PostMedia> postMediaList = postMediaRepository.findByPostIdOrderByIdAsc(postId);
        List<PostMediaDto> postMediaDtoList = new ArrayList<>();
        for (PostMedia postMedia : postMediaList) {
            PostMediaDto postMediaDto = PostMediaDto.of(postMedia);
            postMediaDtoList.add(postMediaDto);
        }

        Post post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);
        PostFormDto postFormDto = PostFormDto.of(post);

        postFormDto.setViews(post.getViews());
        postFormDto.setLikes(post.getLikes());
        postFormDto.setComments(post.getComments());
        postFormDto.setBoardType(post.getBoardType());

        postFormDto.setPostMediaDtoList(postMediaDtoList);

        // 상세페이지에 표시할 작성자, 작성자ID 가져오기
        String createdBy = post.getCreatedBy(); // BaseEntity의 @CreatedBy 값(username)
        LocalDateTime createdDate = post.getCreateTime();
        Member m = memberRepository.findByUserId(createdBy).orElse(null);

        if (m != null) { // 혹시 못 찾을 수도 있으니 null 체크
            postFormDto.setAuthorId(m.getUserId()); // 로그인 아이디
            postFormDto.setAuthor(m.getName()); // 이름/닉네임
        } else {
            // 작성자가 탈퇴했거나 Member 못 찾을 때
            postFormDto.setAuthorId(createdBy);
            postFormDto.setAuthor(createdBy);
        }
        postFormDto.setCreateTime(createdDate);
        return postFormDto;
    }


    public Long updatePost(BoardType boardType, PostFormDto postFormDto, List<MultipartFile> postMediaFileList, List<MultipartFile> addFile) throws Exception {

        // 게시글 수정
        Post post = postRepository.findById(postFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        post.updatePost(postFormDto);

        // 1) 기존 미디어 교체
        List<Long> ids = (postFormDto.getPostMediaIds() != null)
                ? postFormDto.getPostMediaIds() : List.of();
        List<MultipartFile> repl = (postMediaFileList != null)
                ? postMediaFileList : List.of();
        int n = Math.min(ids.size(), repl.size());
        for (int i = 0; i < n; i++) {
            MultipartFile f = repl.get(i);
            if (f != null && !f.isEmpty()) {
                postMediaService.updatePostMedia(ids.get(i), f);
            }
        }


        // 2) 새 미디어 추가
        List<MultipartFile> adds = (addFile != null) ? addFile : List.of();
        for (MultipartFile f : adds) {
            if (f == null || f.isEmpty()) continue;
            PostMedia pm = new PostMedia();
            pm.setPost(post);
            postMediaService.savePostMedia(pm, f);
            // 선택: 양방향 컬렉션에 추가(디버깅/영속성 보장용)
            post.getMediaList().add(pm);
        }
        return post.getId();
    }

    // 게시글 소유자 확인
    private void ensureOwner(String ownerUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = (auth != null ? auth.getName() : null);
        if (current == null || !current.equals(ownerUserId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
    }

    // === 논리삭제 : deleted = true 만 설정 ===
    public BoardType deletePostSoft(Long postId) {
        // 1) 게시글 로드 (삭제되지 않은 것만)
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 2) 권한 체크 (작성자 or 관리자)
        ensureOwner(post.getCreatedBy());

        // 3) 논리삭제
        post.setDeleted(true);
        // flush는 트랜잭션 종료 시점에 반영됨

        return post.getBoardType(); // 컨트롤러에서 목록으로 리다이렉트용
    }


    // === 물리삭제: 첨부파일까지 실제 파일/레코드 삭제 ===
//    public BoardType deletePostHard(Long postId) throws Exception {
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
//
//        ensureOwnerOrAdmin(post.getCreatedBy());
//
//        // 1) 첨부 미디어 파일 삭제 (DB 삭제 전에 실제 파일부터 제거)
//        List<PostMedia> medias = postMediaRepository.findByPostIdOrderByIdAsc(postId);
//        for (PostMedia media : medias) {
//            postMediaService.deletePhysical(media); // 아래에 메서드 추가 예시
//        }
//
//        // 2) 게시글 삭제 (cascade = ALL + orphanRemoval=true 라면 미디어 엔티티도 같이 삭제됨)
//        postRepository.delete(post);
//
//        return post.getBoardType();
//    }

    // 조회수 증가
    @Transactional
    public void increaseViews(Long postId, HttpServletRequest req) {
        // 세션 가드/조건 전부 제거하고 항상 +1
        int updated = postRepository.incrementViews(postId);
        if (updated == 0) {
            // 혹시 파라미터 미스/락 등으로 0건이면 네이티브로 폴백
            try { postRepository.incrementViewsNative(postId); } catch (Exception ignore) {}
        }
    }



    // 미디어 실제 저장 경로
//    @Value("${mediaLocation}")
//    private String uploadDir;
//
//    public Long create(PostFormDto postCreateDto, Member author) throws IOException {
//        Post post = new Post();
//        post.setBoardType(postCreateDto.getBoardType());
//        post.setTitle(postCreateDto.getTitle());
//        post.setContent(postCreateDto.getContent());
//        post.setAuthor(author.getName());
//        post.setAuthorId(author.getUserId());
//        post.setIpAddress(postCreateDto.getIpAddress());
//
//        // 이미지 저장
//        if (postCreateDto.getImages() != null) {
//            for (MultipartFile file : postCreateDto.getImages()) {
//                if (file.isEmpty()) continue;
//                PostMedia media = storeFile(file, MediaType.IMAGE);
//                media.setPost(post);
//                post.getMediaList().add(media);
//            }
//        }
//        // 동영상 저장
//        if (postCreateDto.getVideos() != null) {
//            for (MultipartFile file : postCreateDto.getVideos()) {
//                if (file.isEmpty()) continue;
//                PostMedia media = storeFile(file, MediaType.VIDEO);
//                media.setPost(post);
//                post.getMediaList().add(media);
//            }
//        }
//
//        return postRepository.save(post).getId();
//    }
//
//    private PostMedia storeFile(MultipartFile file, MediaType mediaType) throws IOException {
//        // 확장자 추출
//        String ext = Optional.ofNullable(file.getOriginalFilename())
//                .filter(fn -> fn.contains("."))
//                .map(fn -> fn.substring(fn.lastIndexOf('.') + 1))
//                .orElse("");
//        String stored = UUID.randomUUID() + (ext.isBlank() ? "" : "." + ext);
//
//        // /var/www/uploads/2025/08/24/VIDEO/UUID.mp4 형태로 저장 예시
//        String datePath = LocalDate.now().toString().replace("-", "/");   // 2025/08/24
//        // 최종 저장 디렉터리: {uploadDir}/{YYYY/MM/DD}/{IMAGE|VIDEO}
//        Path dir = Paths.get(uploadDir, datePath, mediaType.name());
//        // 디렉터리 없으면 생성(여러 단계도 한 번에)
//        Files.createDirectories(dir);
//
//        Path path = dir.resolve(stored);
//        file.transferTo(path.toFile());     // 실제 파일 데이터 저장(스트리밍)
//
//        // DB에 저장할 미디어의 메타정보
//        PostMedia postMedia = new PostMedia();
//        postMedia.setMediaType(mediaType);
//        postMedia.setOriginalName(file.getOriginalFilename());
//        postMedia.setStoredName(stored);
//        postMedia.setSize(file.getSize());
//        postMedia.setContentType(file.getContentType());
//
//        // 정적 URL (예: Nginx가 /uploads → ${app.upload-dir} 매핑)
//        String url = "/uploads/" + datePath + "/" + mediaType.name() + "/" + stored;
//        postMedia.setUrl(url);  // 뷰에서 <img src>나 <video src>로 사용
//
//        return postMedia;
//    }
}
