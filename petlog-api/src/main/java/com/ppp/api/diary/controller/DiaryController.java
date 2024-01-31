package com.ppp.api.diary.controller;

import com.ppp.api.diary.dto.request.DiaryRequest;
import com.ppp.api.diary.service.DiaryService;
import com.ppp.common.security.PrincipalDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets")
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping(value = "/{petId}/diaries")
    private ResponseEntity<Void> createDiary(@PathVariable Long petId,
                                             @Valid @RequestPart DiaryRequest request,
                                             @Valid @RequestPart(required = false)
                                             @Size(max = 10, message = "이미지는 10개 이하로 첨부해주세요.") List<MultipartFile> images,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryService.createDiary(principalDetails.getUser(), petId, request, images);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/diaries/{diaryId}")
    private ResponseEntity<Void> updateDiary(@PathVariable Long diaryId,
                                             @Valid @RequestPart DiaryRequest request,
                                             @Valid @RequestPart(required = false)
                                             @Size(max = 10, message = "이미지는 10개 이하로 첨부해주세요.") List<MultipartFile> images,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryService.updateDiary(principalDetails.getUser(), diaryId, request, images);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/diaries/{diaryId}")
    private ResponseEntity<Void> deleteDiary(@PathVariable Long diaryId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        diaryService.deleteDiary(principalDetails.getUser(), diaryId);
        return ResponseEntity.ok().build();
    }
}
