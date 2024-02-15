package com.ppp.domain.user;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class UserDao {
    private String id;
    private String nickname;

    @QueryProjection
    public UserDao(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
