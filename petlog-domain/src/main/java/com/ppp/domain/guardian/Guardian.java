package com.ppp.domain.guardian;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.constant.RepStatus;
import com.ppp.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Guardian extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RepStatus repStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Guardian(RepStatus repStatus, Pet pet, User user) {
        this.repStatus = repStatus;
        this.pet = pet;
        this.user = user;
    }
}
