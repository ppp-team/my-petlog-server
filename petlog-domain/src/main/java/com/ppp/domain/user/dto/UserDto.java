package com.ppp.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String nickname;

    @QueryProjection
    public UserDto(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
