package com.matchday.external.fileService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    
    /**
     * 파일을 스토리지에 업로드하고 다운로드 URL을 반환
     * 
     * @param file 업로드할 파일
     * @param path 스토리지 내 저장 경로
     * @return 업로드된 파일의 다운로드 URL
     * @throws IOException 파일 업로드 중 오류 발생 시
     */
    String uploadFile(MultipartFile file, String path) throws IOException;
    
    /**
     * 스토리지에서 파일을 삭제
     * 
     * @param path 삭제할 파일의 스토리지 경로
     * @throws IOException 파일 삭제 중 오류 발생 시
     */
    void deleteFile(String path) throws IOException;
    
    /**
     * 파일의 다운로드 URL을 반환
     * 
     * @param path 파일의 스토리지 경로
     * @return 파일의 다운로드 URL
     * @throws IOException URL 생성 중 오류 발생 시
     */
    String getDownloadUrl(String path) throws IOException;
}