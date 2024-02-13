package com.ppp.domain.invitation;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.invitation.constant.InviteStatus;
import com.ppp.domain.pet.Pet;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Invitation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, updatable = false)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private InviteStatus inviteStatus;

    @Column(length = 100, nullable = false)
    private String inviterId;                               // 초대를 한 사람

    @Column(length = 100, nullable = false)
    private String inviteeId;                               // 초대를 받은 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    public void updateInviteStatus(InviteStatus inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    @Builder
    public Invitation(String inviteCode, InviteStatus inviteStatus, Pet pet, String inviterId, String inviteeId) {
        this.inviteCode = inviteCode;
        this.inviteStatus = inviteStatus;
        this.pet = pet;
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
    }
}
