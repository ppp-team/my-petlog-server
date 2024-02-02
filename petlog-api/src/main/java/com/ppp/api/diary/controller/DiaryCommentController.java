package com.ppp.api.diary.controller;

import com.ppp.api.diary.dto.request.DiaryCommentRequest;
import com.ppp.api.diary.service.DiaryCommentService;
import com.ppp.common.security.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets/diaries")
public class DiaryCommentController {
    private final DiaryCommentService diaryCommentService;

    @PostMapping(value = "/{diaryId}/comments")
    private ResponseEntity<Void> createComment(@PathVariable Long diaryId,
                                               @Valid @RequestBody DiaryCommentRequest request,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.createComment(principalDetails.getUser(), diaryId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/comments/{commentId}")
    private ResponseEntity<Void> updateComment(@PathVariable Long commentId,
                                               @Valid @RequestBody DiaryCommentRequest request,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.updateComment(principalDetails.getUser(), commentId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/comments/{commentId}")
    private ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryCommentService.deleteComment(principalDetails.getUser(), commentId);
        return ResponseEntity.ok().build();
    }
}
