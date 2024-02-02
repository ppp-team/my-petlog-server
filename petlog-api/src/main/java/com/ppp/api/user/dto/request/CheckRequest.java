package com.ppp.api.user.dto.request;

import lombok.Data;

@Data
public class CheckRequest {
    private String nickname;
    private String email;
}