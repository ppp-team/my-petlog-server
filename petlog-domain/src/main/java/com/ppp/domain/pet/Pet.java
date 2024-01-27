package com.ppp.domain.pet;

import com.ppp.domain.pet.constant.Gender;
import com.ppp.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 20)
    private String invitedCode;

    @Column(length = 100)
    private String name;

    @Column(length = 50)
    private String type;

    @Column(length = 100)
    private String breed;

    @Enumerated(EnumType.STRING)
    private Gender gender; // "male" or "female"

    @Column(columnDefinition = "BIT default 0")
    private Boolean isNeutered;

    private LocalDateTime birth;

    private LocalDateTime firstMeetDate;

    @Column(length = 50)
    private String registNumber;

    @Column(length = 1)
    private String repStatus;

    @Column(columnDefinition = "BIT default 0")
    private Boolean isDeleted;
}
