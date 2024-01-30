package com.ppp.api.diary.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryRequest {
    @Size(min = 1, max = 255, message = "1자 이상 255자 이하의 제목을 입력해주세요.")
    private String title;

    @Size(min = 1, max = 5000, message = "1자 이상 5000자 이하의 내용을 입력해주세요.")
    private String content;

    @PastOrPresent(message = "날짜를 확인해주세요.")
    private LocalDate date;

    @JsonIgnore
    private List<MultipartFile> medias = new ArrayList<>();

    public void addMedias(List<MultipartFile> images, List<MultipartFile> videos) {
        if (images != null && !images.isEmpty())
            medias.addAll(images);
        if (videos != null && !videos.isEmpty())
            medias.addAll(videos);
    }
}
