package com.matchday.external.fileService.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.matchday.external.fileService.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
public class FirebaseStorageService implements StorageService {
    
    private final Storage storage;
    private final String bucketName;
    
    public FirebaseStorageService(@Value("${firebase.storage.bucket-name}") String bucketName) {
        this.bucketName = bucketName;
        this.storage = StorageOptions.getDefaultInstance().getService();
    }
    
    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {
        // 파일 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 고유한 파일명 생성
        String fileName = path + "/" + UUID.randomUUID() + extension;
        
        // BlobId 생성
        BlobId blobId = BlobId.of(bucketName, fileName);
        
        // BlobInfo 생성 (메타데이터 포함)
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        
        try {
            // 파일 업로드
            Blob blob = storage.create(blobInfo, file.getBytes());
            log.info("파일 업로드 성공: {}", fileName);
            
            // 공개 접근 가능한 URL 생성 (유효기간 없음)
            return getPublicUrl(fileName);
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", fileName, e);
            throw new IOException("파일 업로드에 실패했습니다: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteFile(String path) throws IOException {
        try {
            BlobId blobId = BlobId.of(bucketName, path);
            boolean deleted = storage.delete(blobId);
            
            if (deleted) {
                log.info("파일 삭제 성공: {}", path);
            } else {
                log.warn("파일을 찾을 수 없음: {}", path);
                throw new IOException("삭제할 파일을 찾을 수 없습니다: " + path);
            }
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", path, e);
            throw new IOException("파일 삭제에 실패했습니다: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getDownloadUrl(String path) throws IOException {
        return getPublicUrl(path);
    }
    
    /**
     * 공개 접근 가능한 URL 생성 (유효기간 없음)
     * Firebase Storage Rules에서 공개 읽기가 설정되어 있어야 함
     */
    private String getPublicUrl(String path) throws IOException {
        try {
            // URL 인코딩하여 공개 접근 가능한 URL 생성
            String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8);
            String publicUrl = String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName,
                encodedPath
            );
            
            log.debug("공개 URL 생성 완료: {}", publicUrl);
            return publicUrl;
            
        } catch (Exception e) {
            log.error("공개 URL 생성 실패: {}", path, e);
            throw new IOException("공개 URL 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }
}