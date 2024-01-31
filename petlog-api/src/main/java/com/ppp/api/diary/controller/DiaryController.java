package com.ppp.api.diary.controller;

import com.ppp.api.diary.dto.request.DiaryRequest;
import com.ppp.api.diary.service.DiaryService;
import com.ppp.api.exception.ExceptionResponse;
import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Diary", description = "Diary APIs")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets")
public class DiaryController {
    private final DiaryService diaryService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "일치하는 리소스 없음.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "해당 기록 공간에 대한 권한이 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping(value = "/{petId}/diaries", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ResponseEntity<Void> createDiary(@PathVariable Long petId,
                                             @Valid @RequestPart DiaryRequest request,
                                             @Parameter(description = "multipart form data 형식의 이미지를 등록해주세요.", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                             @Valid @RequestPart(required = false)
                                             @Size(max = 10, message = "이미지는 10개 이하로 첨부해주세요.") List<MultipartFile> images,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryService.createDiary(principalDetails.getUser(), petId, request, images);
        return ResponseEntity.ok().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "일치하는 리소스 없음.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "해당 기록 공간에 대한 권한이 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PutMapping(value = "/diaries/{diaryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ResponseEntity<Void> updateDiary(@PathVariable Long diaryId,
                                             @Valid @RequestPart DiaryRequest request,
                                             @Parameter(description = "multipart form data 형식의 이미지를 등록해주세요.", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                             @Valid @RequestPart(required = false)
                                             @Size(max = 10, message = "이미지는 10개 이하로 첨부해주세요.") List<MultipartFile> images,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryService.updateDiary(principalDetails.getUser(), diaryId, request, images);
        return ResponseEntity.ok().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "일치하는 리소스 없음.", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "해당 기록 공간에 대한 권한이 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @DeleteMapping(value = "/diaries/{diaryId}")
    private ResponseEntity<Void> deleteDiary(@PathVariable Long diaryId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryService.deleteDiary(principalDetails.getUser(), diaryId);
        return ResponseEntity.ok().build();
    }
}
