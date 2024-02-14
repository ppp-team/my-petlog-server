package com.ppp.api.video.controller;

import com.ppp.api.exception.ExceptionResponse;
import com.ppp.api.video.dto.response.VideoResponse;
import com.ppp.api.video.service.VideoManageService;
import com.ppp.common.security.PrincipalDetails;
import com.ppp.common.validator.EnumValue;
import com.ppp.domain.common.constant.Domain;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@Slf4j
@Tag(name = "Temp Video", description = "Temp Video APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/videos")
@MultipartConfig(maxFileSize = 1024 * 1024 * 100, fileSizeThreshold = 1024 * 1024 * 100)
public class VideoController {
    private final VideoManageService videoManageService;

    @Operation(summary = "임시 비디오 업로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = VideoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 반려동물 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoResponse> uploadTempVideo(@RequestParam(defaultValue = "DIARY") @EnumValue(enumClass = Domain.class) String domain,
                                                         @RequestPart MultipartFile video,
                                                         @AuthenticationPrincipal PrincipalDetails principalDetail) {
        return ResponseEntity.ok(videoManageService.uploadTempVideo(principalDetail.getUser(), domain, video));
    }
}
