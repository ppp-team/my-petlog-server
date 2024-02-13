package com.ppp.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailRequest {
    @Email
    @Schema(description = "이메일", nullable = false, example = "abc@test.com")
    private String email;
}
