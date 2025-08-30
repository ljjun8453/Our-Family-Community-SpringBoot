package com.community.controller;


import com.community.constant.BoardType;
import com.community.dto.PostFormDto;
import com.community.entity.Post;
import com.community.repository.CommentRepository;
import com.community.repository.MemberRepository;
import com.community.repository.PostLikeRepository;
import com.community.service.CommentService;
import com.community.service.LikeService;
import com.community.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final PostLikeRepository postLikeRepository;
    private final LikeService likeService;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    // 글 목록가기
    @GetMapping("/{boardType}")
    public String list(@PathVariable BoardType boardType,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        Page<Post> posts = postService.listPosts(boardType, page, 10);

        model.addAttribute("boardType", boardType);
        model.addAttribute("posts", posts);
        return "board/list";
    }

    // 게시글 작성 페이지로 가기
    @GetMapping("/{boardType}/new")
    public String postForm(@PathVariable BoardType boardType, Model model) {
        model.addAttribute("boardType", boardType);
        model.addAttribute("postFormDto", new PostFormDto());
        return "board/write";
    }

    // 게시글 작성하기
    @PostMapping("/{boardType}/new")
    public String postNew(@PathVariable BoardType boardType, @Valid PostFormDto postFormDto, BindingResult bindingResult, Model model, @RequestParam("postMediaFile") List<MultipartFile> postMediaFileList, HttpServletRequest request, RedirectAttributes rttr) {

        if(bindingResult.hasErrors()) {
            model.addAttribute("boardType", boardType);
            return "board/write";
        }

        // ★ IP 세팅
        postFormDto.setIpAddress(getClientIp(request));

        try {
            model.addAttribute("boardType", boardType);
            postService.savePost(boardType, postFormDto, postMediaFileList);
        } catch (Exception e) {
            model.addAttribute("boardType", boardType);
            model.addAttribute("errorMessage", "게시글 등록 중 에러가 발생했습니다.");
            return "board/write";
        }
        rttr.addFlashAttribute("alert", "게시글이 등록되었습니다.");
        return "redirect:/board/" + boardType;
    }

    // 게시글 수정 페이지로 가기
    @GetMapping("/{boardType}/{postId:\\d+}/edit")
    public String postDtl(@PathVariable BoardType boardType, @PathVariable Long postId, Model model) {

        try {
            PostFormDto postFormDto = postService.getPostDtl(postId);
            model.addAttribute("boardType", boardType);
            model.addAttribute("postFormDto", postFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("boardType", boardType);
            model.addAttribute("errorMessage", "존재하지 않는 게시글 입니다.");
            model.addAttribute("postFormDto", new PostFormDto());
            return "board/write";
        }
        return "board/write";
    }

    // 게시글 수정하기
    @PostMapping("/{boardType}/{postId:\\d+}")
    public String postUpdate(@PathVariable BoardType boardType,
                             @PathVariable Long postId,
                             @Valid @ModelAttribute PostFormDto postFormDto,
                             BindingResult bindingResult,
                             @RequestParam(value = "postMediaFile", required = false) List<MultipartFile> postMediaFileList,
                             @RequestParam(value = "postMediaAdd",  required = false) List<MultipartFile> addFile,
                             Model model,
                             HttpServletRequest request,
                             RedirectAttributes rttr){

        if(bindingResult.hasErrors()) {
            model.addAttribute("boardType", boardType);
            System.out.println("Update errors => " + bindingResult.getAllErrors());
            return "board/write";
        }

        // ★ IP 세팅
        postFormDto.setIpAddress(getClientIp(request));

        try {
            model.addAttribute("boardType", boardType);
            postFormDto.setId(postId);
            postService.updatePost(boardType, postFormDto, postMediaFileList, addFile);
        } catch (Exception e) {
            model.addAttribute("boardType", boardType);
            model.addAttribute("errorMessage", "게시글 수정 중 에러가 발생했습니다.");
            return "board/write";
        }
        rttr.addFlashAttribute("alert", "게시글 수정이 완료되었습니다.");
        return "redirect:/board/" + boardType;
    }


    // 게시글 상세페이지 조회
    @GetMapping("/{boardType}/{postId:\\d+}")
    public String postDtl(@PathVariable BoardType boardType, HttpServletRequest request, Model model, @PathVariable("postId") Long postId, Authentication auth){
        // ✅ 조회수 증가 (세션당 1회)
        postService.increaseViews(postId, request);

        // 게시글/댓글 로드
        PostFormDto postFormDto = postService.getPostDtl(postId);

        // ✅ 즉시 정확한 숫자 보장(집계로 덮어쓰기)
        long likeCount    = postLikeRepository.countByPost_Id(postId);
        long commentCount = commentRepository.countByPostId(postId);
        postFormDto.setLikes(likeCount);
        postFormDto.setComments(commentCount);


        boolean isLiked = (auth != null)
                && postLikeRepository.existsByPost_IdAndMember_UserId(postId, auth.getName());

        model.addAttribute("boardType", boardType);
        model.addAttribute("post", postFormDto);
        model.addAttribute("comments", commentService.comments(postId));
        model.addAttribute("isLiked", isLiked);
        return "board/view";
    }

    // 추천수 한 번만
    @PostMapping("/{boardType}/{postId:\\d+}/like")
    public String likeOnce(@PathVariable BoardType boardType,
                           @PathVariable Long postId,
                           Authentication auth,
                           HttpServletRequest request,
                           RedirectAttributes rttr) {
        if (auth == null) {
            rttr.addFlashAttribute("alert", "로그인 후 이용해 주세요.");
            return "redirect:/login";
        }

        // 단순 카운트업 postRepository.incrementLikes(postId);
        boolean created = likeService.likeOnce(postId, auth.getName());
        request.getSession().setAttribute(
                "alert",
                created ? "추천! 👍 성공!" : "이미 추천한 글입니다."
        );
//        rttr.addFlashAttribute("alert", created ? "추천! 👍 성공!" : "이미 추천한 글입니다.");
        return "redirect:/board/" + boardType + "/" + postId;
    }

    // 논리삭제
    @PostMapping("/{boardType}/{postId:\\d+}/delete")
    public String deleteSoft(@PathVariable BoardType boardType, @PathVariable Long postId, RedirectAttributes rttr) {
        postService.deletePostSoft(postId);
        rttr.addFlashAttribute("alert", "게시글이 삭제되었습니다.");
        return "redirect:/board/" + boardType;
    }

    // 물리삭제
//    @PostMapping("/{boardType}/{postId}/destroy")
//    public String deleteHard(@PathVariable BoardType boardType, @PathVariable Long postId, RedirectAttributes rttr) throws Exception {
//        BoardType boardType = postService.deletePostHard(id);
//        rttr.addFlashAttribute("msg", "게시글이 완전히 삭제되었습니다.");
//        return "redirect:/board/" + boardType;
//    }


    // 작성자 IP 추출
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            // 프록시 체인일 경우 맨 앞이 실제 클라이언트 IP
            int comma = ip.indexOf(',');
            return comma > -1 ? ip.substring(0, comma).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        return (ip != null && !ip.isBlank()) ? ip : request.getRemoteAddr();
    }
}
