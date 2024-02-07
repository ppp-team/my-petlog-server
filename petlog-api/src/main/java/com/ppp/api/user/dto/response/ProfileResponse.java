package com.ppp.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProfileResponse {
    private String id;
    private String email;
    private String nickname;
    private String profilePath;
}
