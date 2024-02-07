package com.ppp.domain.user;

import com.ppp.domain.common.BaseDocument;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Document(indexName = "users")
public class UserDocument extends BaseDocument {
    private String nickname;

    @Builder
    public UserDocument(String id, String nickname) {
        super(id);
        this.nickname = nickname;
    }

    public static UserDocument from(User user) {
        return UserDocument.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}
