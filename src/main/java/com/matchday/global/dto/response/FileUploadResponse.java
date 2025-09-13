package com.matchday.global.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileUploadResponse {
    
    private final String fileName;
    private final String fileUrl;
    private final String filePath;
    private final Long fileSize;
    private final String contentType;
    
    public static FileUploadResponse of(String fileName, String fileUrl, String filePath, 
                                      Long fileSize, String contentType) {
        return FileUploadResponse.builder()
                .fileName(fileName)
                .fileUrl(fileUrl)
                .filePath(filePath)
                .fileSize(fileSize)
                .contentType(contentType)
                .build();
    }
}