package com.ppp.api.mock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockRequestDto {
    @NotBlank
    @Size(min = 1, max = 150, message = "1-150자의 제목을 작성해주세요.")
    private String title;
    @NotBlank
    @Size(min = 1, max = 1000, message = "1-1000자의 내용을 작성해주세요.")
    private String content;
}
