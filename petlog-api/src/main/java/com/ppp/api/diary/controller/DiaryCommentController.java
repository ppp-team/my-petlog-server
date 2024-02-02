package com.ppp.api.diary.controller;

import com.ppp.api.diary.dto.request.DiaryCommentRequest;
import com.ppp.api.diary.dto.response.DiaryCommentResponse;
import com.ppp.api.diary.service.DiaryCommentService;
import com.ppp.common.security.PrincipalDetails;
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

    @PostMapping(value = "/{diaryId}/comments")
    private ResponseEntity<Void> createComment(@PathVariable Long petId,
                                               @PathVariable Long diaryId,
                                               @Valid @RequestBody DiaryCommentRequest request,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.createComment(principalDetails.getUser(), petId, diaryId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/comments/{commentId}")
    private ResponseEntity<Void> updateComment(@PathVariable Long petId,
                                               @PathVariable Long commentId,
                                               @Valid @RequestBody DiaryCommentRequest request,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.updateComment(principalDetails.getUser(), petId, commentId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/comments/{commentId}")
    private ResponseEntity<Void> deleteComment(@PathVariable Long petId,
                                               @PathVariable Long commentId,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.deleteComment(principalDetails.getUser(), petId, commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{diaryId}/comments")
    private ResponseEntity<List<DiaryCommentResponse>> displayComments(@PathVariable Long petId,
                                                                       @PathVariable Long diaryId,
                                                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity
                .ok(diaryCommentService.displayComments(principalDetails.getUser(), petId, diaryId));
    }
}
