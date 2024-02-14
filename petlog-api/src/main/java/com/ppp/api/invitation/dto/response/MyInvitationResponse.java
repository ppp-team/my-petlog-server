package com.ppp.api.invitation.dto.response;

import com.ppp.common.util.TimeUtil;
import com.ppp.domain.invitation.Invitation;
import com.ppp.domain.invitation.constant.InviteStatus;
import com.ppp.domain.user.ProfileImage;
import com.ppp.domain.user.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyInvitationResponse {
    private Long invitationId;
    private String inviterName;
    private String inviteStatus;
    private String profilePath;
    private String invitedAt;

    public static MyInvitationResponse from(Invitation invitation, User user, ProfileImage profileImage) {
        return MyInvitationResponse.builder()
                .invitationId(invitation.getId())
                .inviterName(user.getNickname())
                .inviteStatus(InviteStatus.PENDING.getValue())
                .profilePath(profileImage.getUrl())
                .invitedAt(TimeUtil.calculateTerm(invitation.getCreatedAt()))
                .build();
    }
}