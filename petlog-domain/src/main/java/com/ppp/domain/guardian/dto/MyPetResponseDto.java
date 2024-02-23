package com.ppp.domain.guardian.dto;

import com.ppp.domain.pet.constant.Gender;
import com.ppp.domain.guardian.constant.RepStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MyPetResponseDto {
    private Long petId;

    private String ownerId;

    private String invitedCode;

    private String name;

    private String type;

    private String breed;

    private Gender gender;

    private Boolean isNeutered;

    private LocalDateTime birth;

    private LocalDateTime firstMeetDate;

    private Double weight;

    private String registeredNumber;

    private RepStatus repStatus;

    private String petImageUrl;

    @Builder
    @QueryProjection
    public MyPetResponseDto(Long petId, String ownerId, String invitedCode, String name, String type, String breed, Gender gender, Boolean isNeutered, LocalDateTime birth, LocalDateTime firstMeetDate, Double weight, String registeredNumber, RepStatus repStatus, String petImageUrl) {
        this.petId = petId;
        this.ownerId = ownerId;
        this.invitedCode = invitedCode;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.gender = gender;
        this.isNeutered = isNeutered;
        this.birth = birth;
        this.firstMeetDate = firstMeetDate;
        this.weight = weight;
        this.registeredNumber = registeredNumber;
        this.repStatus = repStatus;
        this.petImageUrl = petImageUrl;
    }
}
