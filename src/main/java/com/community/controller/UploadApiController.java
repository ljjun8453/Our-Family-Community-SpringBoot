package com.community.controller;

import com.community.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UploadApiController {

    private final FileService fileService; // 저장/검증 유틸

    @Value("${mediaLocation}") // 실제 디스크 경로
    private String uploadDir;

    @PostMapping("/uploads")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        // 확장자 검증(이미 FileService에 메서드 있으면 그거 사용)
        fileService.validateExtension(file.getOriginalFilename());

        // 저장 (반환: 저장 파일명)
        String saved = fileService.uploadFile(uploadDir, file.getOriginalFilename(), file.getBytes());

        // 브라우저가 접근할 URL. ResourceHandler에서 /media/** 로 노출중이라면:
        String url = "/media/upload/" + saved;

        return Map.of("url", url);
    }
}
