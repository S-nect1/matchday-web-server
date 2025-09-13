package com.matchday.external.controller;

import com.matchday.global.dto.response.FileUploadResponse;
import com.matchday.global.entity.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "파일 관리", description = "파일 업로드 API")
public interface FileControllerDocs {

    @Operation(
        summary = "파일 업로드",
        description = "이미지 파일을 업로드합니다. jpg, jpeg, png 형식만 지원하며 최대 500KB까지 업로드 가능합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기 초과"),
            @ApiResponse(responseCode = "500", description = "파일 업로드 실패")
        }
    )
    @PostMapping("/upload")
    BaseResponse<FileUploadResponse> uploadFile(
        @Parameter(description = "업로드할 파일 (jpg, jpeg, png만 지원, 최대 500KB)")
        @RequestParam("file") MultipartFile file,
        @Parameter(description = "파일 카테고리", example = "team")
        @RequestParam("category") String category,
        @RequestHeader("User-Id") Long userId
    ) throws IOException;
}