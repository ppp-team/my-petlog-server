package com.ppp.api.video.controller;

import com.ppp.api.video.dto.response.VideoResponse;
import com.ppp.api.video.service.VideoManageService;
import com.ppp.common.security.PrincipalDetails;
import com.ppp.common.validator.EnumValue;
import com.ppp.domain.common.constant.Domain;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/videos")
@MultipartConfig(maxFileSize = 1024 * 1024 * 100, fileSizeThreshold = 1024 * 1024 * 100)
public class VideoController {
    private final VideoManageService videoManageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoResponse> uploadTempVideo(@RequestParam(defaultValue = "DIARY") @EnumValue(enumClass = Domain.class) String domain,
                                                              @RequestPart MultipartFile video,
                                                              @AuthenticationPrincipal PrincipalDetails principalDetail) {
        return ResponseEntity.ok(videoManageService.uploadTempVideo(principalDetail.getUser(), domain, video));
    }
}
