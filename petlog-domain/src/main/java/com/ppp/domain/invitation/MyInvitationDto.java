package com.ppp.domain.invitation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MyInvitationDto {
    private Long invitationId;
    private String inviteeName;
    private String inviteStatus;
    private String profilePath;
    private String invitedAt;
    @JsonIgnore
    private LocalDateTime createdAt;

    @QueryProjection
    public MyInvitationDto(Long invitationId, String inviteeName, String inviteStatus, String profilePath, LocalDateTime createdAt) {
        this.invitationId = invitationId;
        this.inviteeName = inviteeName;
        this.inviteStatus = inviteStatus;
        this.profilePath = profilePath;
        this.createdAt = createdAt;
    }
}
