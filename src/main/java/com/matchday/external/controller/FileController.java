package com.matchday.external.controller;

import com.matchday.external.exception.ExternalControllerAdvice;
import com.matchday.common.dto.response.FileUploadResponse;
import com.matchday.common.entity.BaseResponse;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.external.fileService.StorageService;
import com.matchday.security.filter.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController implements FileControllerDocs {
    
    private final StorageService storageService;
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png"
    );
    
    private static final long MAX_FILE_SIZE = 500 * 1024; // 500KB
    
    @Override
    @PostMapping("/upload")
    public BaseResponse<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            @AuthenticationPrincipal JwtUserPrincipal userPrincipal) throws IOException {
        
        // 파일 유효성 검사
        validateFile(file);

        // 카테고리별 경로 생성
        String basePath = generateFilePath(category, userPrincipal.getUserId());

        // 파일 업로드
        String fileUrl = storageService.uploadFile(file, basePath);

        // 응답 생성
        FileUploadResponse response = FileUploadResponse.of(
            file.getOriginalFilename(),
            fileUrl,
            basePath,
            file.getSize(),
            file.getContentType()
        );

        log.info("파일 업로드 성공 - 사용자: {}, 카테고리: {}, 파일명: {}",
                userPrincipal.getUserId(), category, file.getOriginalFilename());

        return BaseResponse.onSuccess(response, ResponseCode.OK);
    }
    
    private void validateFile(MultipartFile file) {
        // 파일 존재 확인
        if (file == null || file.isEmpty()) {
            throw new ExternalControllerAdvice(ResponseCode._BAD_REQUEST);
        }
        
        // 파일 크기 확인
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ExternalControllerAdvice(ResponseCode.FILE_MAX_SIZE_OVER);
        }
        
        // 파일 타입 확인 (jpg, jpeg, png만 허용)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new ExternalControllerAdvice(ResponseCode.FILE_CONTENT_TYPE_NOT_IMAGE);
        }
        
        // 파일명 확인
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new ExternalControllerAdvice(ResponseCode._BAD_REQUEST);
        }
    }

    private String generateFilePath(String category, Long userId) {
        return switch (category.toLowerCase()) {
            case "team" -> "teams/" + userId + "/profile";
            default -> throw new ExternalControllerAdvice(ResponseCode.FILE_INVALID_CATEGORY);
        };
    }
}