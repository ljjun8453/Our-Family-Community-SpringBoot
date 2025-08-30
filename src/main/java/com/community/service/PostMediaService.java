package com.community.service;

import com.community.entity.PostMedia;
import com.community.repository.PostMediaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class PostMediaService {

    @Value("${mediaLocation}")
    private String mediaLocation;

    private final PostMediaRepository postMediaRepository;

    private final FileService fileService;

    public void savePostMedia(PostMedia postMedia, MultipartFile postMediaFile) throws Exception {
        if (postMediaFile == null || postMediaFile.isEmpty())
            return;

        String originalName = postMediaFile.getOriginalFilename();
        String storedName = "";
        String url = "";
        Long size = 0L;

        //파일 업로드
        if(!StringUtils.isEmpty(originalName)){
            storedName = fileService.uploadFile(mediaLocation, originalName, postMediaFile.getBytes());
            url = "/media/upload/" + storedName;
            size = postMediaFile.getSize();
        }

        // 게시글 미디어 정보 저장
        postMedia.updatePostMedia(originalName, storedName, url, size);
        postMediaRepository.save(postMedia);

    }

    public void updatePostMedia(Long postMediaId, MultipartFile postmediaFile) throws Exception {
        if (!postmediaFile.isEmpty()) {
            PostMedia savedPostMedia = postMediaRepository.findById(postMediaId).orElseThrow(EntityNotFoundException::new);

            // 기존 미디어 파일 삭제
            if (!StringUtils.isEmpty(savedPostMedia.getStoredName())) {
                fileService.deleteFile(mediaLocation+"/"+savedPostMedia.getStoredName());
            }

            String originalName = postmediaFile.getOriginalFilename();
            String storedName = fileService.uploadFile(mediaLocation, originalName, postmediaFile.getBytes());
            String url = "/media/upload/" + storedName;
            savedPostMedia.updatePostMedia(originalName, storedName, url, postmediaFile.getSize());
        }
    }

    // 물리삭제 시 실제 파일 삭제
//    public void deletePhysical(PostMedia media) throws Exception {
//        if (!StringUtils.isEmpty(media.getStoredName())) {
//            fileService.deleteFile(mediaLocation + "/" + media.getStoredName());
//        }
//        // DB에서 미디어 엔티티 삭제
//        postMediaRepository.delete(media);
//    }
}
