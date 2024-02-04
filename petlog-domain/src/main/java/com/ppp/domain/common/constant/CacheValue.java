package com.ppp.domain.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheValue {
    PET_SPACE_AUTHORITY("petSpaceAuthority"),
    DIARY_COMMENT_COUNT("diaryCommentCount"),
    DIARY_LIKE_COUNT("diaryLikeCount"),
    DIARY_COMMENT_LIKE_COUNT("diaryCommentLikeCount")
    ;

    private final String value;
}
