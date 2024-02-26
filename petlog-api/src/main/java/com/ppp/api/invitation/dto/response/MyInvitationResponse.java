package com.ppp.api.invitation.dto.response;

import com.ppp.common.util.TimeUtil;
import com.ppp.domain.invitation.constant.InviteStatus;
import com.ppp.domain.invitation.dto.MyInvitationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyInvitationResponse {
    private Long invitationId;
    private String inviteeName;
    private String inviteStatus;
    private String profilePath;
    private String invitedAt;

    public static MyInvitationResponse from(MyInvitationDto myInvitationDto) {
        String status = null;
        if (InviteStatus.PENDING.name().equals(myInvitationDto.getInviteStatus())) {
            status = InviteStatus.PENDING.getValue();
        } else if (InviteStatus.REJECTED.name().equals(myInvitationDto.getInviteStatus())) {
            status = InviteStatus.REJECTED.getValue();
        }
        return MyInvitationResponse.builder()
                .invitationId(myInvitationDto.getInvitationId())
                .inviteeName(myInvitationDto.getInviteeName())
                .inviteStatus(status)
                .profilePath(myInvitationDto.getProfilePath())
                .invitedAt(TimeUtil.calculateTerm(myInvitationDto.getCreatedAt()))
                .build();
    }
}
