package com.ppp.api.pet.dto.response;

import com.ppp.domain.guardian.constant.RepStatus;
import com.ppp.domain.guardian.dto.MyPetDto;
import com.ppp.domain.pet.constant.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPetResponse {
    @Schema(description = "펫 고유 id")
    private Long petId;

    @Schema(description = "반려동물 주인의 사용자id")
    private String ownerId;

    @Schema(description = "마이펫 초대 코드")
    private String inviteCode;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "타입")
    private String type;

    @Schema(description = "품종")
    private String breed;

    @Schema(description = "성별")
    private Gender gender;

    @Schema(description = "중성화 여부", allowableValues = {"Y", "N"})
    private String isNeutered;

    @Schema(description = "생일")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime birth;

    @Schema(description = "처음 만난 날")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime firstMeetDate;

    @Schema(description = "무게")
    private String weight;

    @Schema(description = "등록번호")
    private String registeredNumber;

    @Schema(description = "대표반려동물")
    private RepStatus repStatus;

    @Schema(description = "반려동물 경로")
    private String petImageUrl;

    public static MyPetResponse from(MyPetDto myPetDto) {

        return MyPetResponse.builder()
                .petId(myPetDto.getPetId())
                .ownerId(myPetDto.getOwnerId())
                .inviteCode(myPetDto.getInvitedCode())
                .name(myPetDto.getName())
                .type(myPetDto.getType())
                .breed(myPetDto.getBreed())
                .gender(myPetDto.getGender())
                .isNeutered(myPetDto.getIsNeutered() == null ? null : (myPetDto.getIsNeutered() ? "Y" : "N"))
                .birth(myPetDto.getBirth())
                .firstMeetDate(myPetDto.getFirstMeetDate())
                .weight(myPetDto.getWeight() == 0 ? null : String.valueOf(myPetDto.getWeight()))
                .registeredNumber(myPetDto.getRegisteredNumber())
                .repStatus(myPetDto.getRepStatus())
                .petImageUrl(myPetDto.getPetImageUrl())
                .build();
    }
}
