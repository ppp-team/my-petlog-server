package com.ppp.api.guardian.dto.response;

import com.ppp.domain.guardian.Guardian;
import com.ppp.domain.guardian.constant.GuardianRole;
import com.ppp.domain.user.ProfileImage;
import lombok.Builder;

@Builder
public record GuardianResponse (
    Long guardianId,
    GuardianRole guardianRole,
    String nickname,
    String profileImageUrl
) {
    public static GuardianResponse from(Guardian guardian, ProfileImage profileImage) {
        return GuardianResponse.builder()
                .guardianId(guardian.getId())
                .guardianRole(guardian.getGuardianRole())
                .nickname(guardian.getUser().getNickname())
                .profileImageUrl(profileImage.getUrl())
                .build();
    }
}
