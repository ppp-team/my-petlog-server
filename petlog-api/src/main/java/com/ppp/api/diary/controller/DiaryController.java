package com.ppp.api.diary.controller;

import com.ppp.api.diary.dto.request.DiaryRequest;
import com.ppp.api.diary.service.DiaryService;
import com.ppp.domain.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/pets")
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping(value = "/{petId}/diaries")
    private ResponseEntity<Void> createDiary(@PathVariable Long petId,
                                             @Valid @RequestPart DiaryRequest request,
                                             @Valid @RequestPart(required = false)
                                             @Size(max = 10, message = "이미지는 10개 이하로 첨부해주세요.") List<MultipartFile> images,
                                             @Valid @RequestPart(required = false)
                                             @Size(max = 1, message = "동영상은 1개 이하로 첨부해주세요.") List<MultipartFile> videos) {
        request.addMedias(images, videos);
        diaryService.createDiary(new User(), petId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/diaries/{diaryId}")
    private ResponseEntity<Void> updateDiary(@PathVariable Long diaryId,
                                             @Valid @RequestPart DiaryRequest request,
                                             @Valid @RequestPart(required = false)
                                             @Size(max = 10, message = "이미지는 10개 이하로 첨부해주세요.") List<MultipartFile> images,
                                             @Valid @RequestPart(required = false)
                                             @Size(max = 1, message = "동영상은 1개 이하로 첨부해주세요.") List<MultipartFile> videos) {
        request.addMedias(images, videos);
        diaryService.updateDiary(new User(), diaryId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/diaries/{diaryId}")
    private ResponseEntity<Void> deleteDiary(@PathVariable Long diaryId) {
        diaryService.deleteDiary(new User(), diaryId);
        return ResponseEntity.ok().build();
    }
}
