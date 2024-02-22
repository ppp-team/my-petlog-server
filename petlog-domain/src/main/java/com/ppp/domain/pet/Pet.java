package com.ppp.domain.pet;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.guardian.Guardian;
import com.ppp.domain.pet.constant.Gender;
import com.ppp.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pet extends BaseTimeEntity {
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
    private Gender gender;

    @Column(columnDefinition = "BIT default 0")
    private Boolean isNeutered;

    private LocalDateTime birth;

    private LocalDateTime firstMeetDate;

    private double weight;

    @Column(length = 50)
    private String registeredNumber;

    @Column(columnDefinition = "BIT default 0")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "pet")
    private List<Guardian> guardianList = new ArrayList<>();

    public void updatePet(String name, String type, String breed, String gender, Boolean isNeutered,
                       LocalDate birth, LocalDate firstMeetDate, double weight, String registeredNumber) {
        if (name != null) {
            this.name = name;
        }
        if (type != null) {
            this.type = type;
        }
        if (breed != null) {
            this.breed = breed;
        }
        if (gender != null) {
            this.gender = gender.equals("MALE") ? Gender.MALE : Gender.FEMALE;
        }
        if (isNeutered != null) {
            this.isNeutered = isNeutered;
        }
        if (birth != null) {
            this.birth = birth.atStartOfDay();
        }
        if (firstMeetDate != null) {
            this.firstMeetDate = firstMeetDate.atStartOfDay();
        }
        if (weight != 0) {
            this.weight = weight;
        }
        if (registeredNumber != null) {
            this.registeredNumber = registeredNumber;
        }
    }
}
