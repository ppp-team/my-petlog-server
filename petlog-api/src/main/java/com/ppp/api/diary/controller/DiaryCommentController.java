package com.ppp.api.diary.controller;

import com.ppp.api.diary.dto.request.DiaryCommentRequest;
import com.ppp.api.diary.dto.response.DiaryCommentResponse;
import com.ppp.api.diary.service.DiaryCommentService;
import com.ppp.api.exception.ExceptionResponse;
import com.ppp.common.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets/{petId}/diaries")
public class DiaryCommentController {
    private final DiaryCommentService diaryCommentService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 일기 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PostMapping(value = "/{diaryId}/comments")
    private ResponseEntity<Void> createComment(@PathVariable Long petId,
                                               @PathVariable Long diaryId,
                                               @Valid @RequestBody DiaryCommentRequest request,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.createComment(principalDetails.getUser(), petId, diaryId, request);
        return ResponseEntity.ok().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "요청 필드 에러", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "댓글 수정 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 댓글 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @PutMapping(value = "/comments/{commentId}")
    private ResponseEntity<Void> updateComment(@PathVariable Long petId,
                                               @PathVariable Long commentId,
                                               @Valid @RequestBody DiaryCommentRequest request,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.updateComment(principalDetails.getUser(), petId, commentId, request);
        return ResponseEntity.ok().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "댓글 수정 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", description = "일치하는 댓글 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @DeleteMapping(value = "/comments/{commentId}")
    private ResponseEntity<Void> deleteComment(@PathVariable Long petId,
                                               @PathVariable Long commentId,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.deleteComment(principalDetails.getUser(), petId, commentId);
        return ResponseEntity.ok().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "기록 공간에 대한 권한 없음", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    @GetMapping(value = "/{diaryId}/comments")
    private ResponseEntity<List<DiaryCommentResponse>> displayComments(@PathVariable Long petId,
                                                                       @PathVariable Long diaryId,
                                                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity
                .ok(diaryCommentService.displayComments(principalDetails.getUser(), petId, diaryId));
    }

    @PostMapping(value = "/comments/{commentId}/like")
    private ResponseEntity<Void> likeComment(@PathVariable Long petId,
                                             @PathVariable Long commentId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.likeComment(principalDetails.getUser(), petId, commentId);
        return ResponseEntity.ok().build();
    }
}
