package com.ppp.domain.invitation;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.invitation.constant.InviteStatus;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.user.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setInviteStatus(InviteStatus inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    @Builder
    public Invitation(String inviteCode, InviteStatus inviteStatus, Pet pet, User user) {
        this.inviteCode = inviteCode;
        this.inviteStatus = inviteStatus;
        this.pet = pet;
        this.user = user;
    }
}
