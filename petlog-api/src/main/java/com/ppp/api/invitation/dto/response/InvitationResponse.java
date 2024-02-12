package com.ppp.api.invitation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvitationResponse {
    private Long invitationId;
    private Long petId;
    private String inviterName;
    private String inviteStatus;
    private String petName;
    private String profilePath;
    private String invitedAt;
}