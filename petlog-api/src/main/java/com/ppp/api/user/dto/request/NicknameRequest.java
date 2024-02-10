package com.ppp.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NicknameRequest {
    @Schema(description = "닉네임", nullable = false)
    private String nickname;
}
