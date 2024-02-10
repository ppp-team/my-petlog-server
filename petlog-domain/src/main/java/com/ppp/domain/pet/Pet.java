package com.ppp.domain.pet;

import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.guardian.Guardian;
import com.ppp.domain.pet.constant.Gender;
import com.ppp.domain.pet.constant.RepStatus;
import com.ppp.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    private RepStatus repStatus;

    @Column(columnDefinition = "BIT default 0")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "pet")
    private List<Guardian> guardianList = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setNeutered(Boolean neutered) {
        isNeutered = neutered;
    }

    public void setBirth(LocalDateTime birth) {
        this.birth = birth;
    }

    public void setFirstMeetDate(LocalDateTime firstMeetDate) {
        this.firstMeetDate = firstMeetDate;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setRegisteredNumber(String registeredNumber) {
        this.registeredNumber = registeredNumber;
    }

    public void setRepStatus(RepStatus repStatus) {
        this.repStatus = repStatus;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
