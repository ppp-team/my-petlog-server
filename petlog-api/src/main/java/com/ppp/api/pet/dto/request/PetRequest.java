package com.ppp.api.pet.dto.request;

import com.ppp.common.validator.EnumValue;
import com.ppp.domain.pet.constant.Gender;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Pet 정보 요청 DTO")
public class PetRequest {
    @NotBlank
    @Schema(description = "이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "타입", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @NotBlank
    @Schema(description = "품종", requiredMode = Schema.RequiredMode.REQUIRED)
    private String breed;

    @EnumValue(enumClass = Gender.class, message = "유효하지 않은 성별입니다.")
    @Schema(description = "성별", allowableValues = {"FEMALE", "MALE"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String gender;

    @Schema(description = "중성화 여부")
    private Boolean isNeutered;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "생년월일(yyyy-MM-dd)")
    private LocalDate birth;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "처음 만난 날(yyyy-MM-dd)")
    private LocalDate firstMeetDate;

    @Min(0)
    @Digits(integer = 5, fraction = 2, message = "2자리 소수점으로 구성되어야 합니다.")
    @Schema(description = "무게")
    private double weight;

    @Schema(description = "등록번호")
    private String registeredNumber;

    @Hidden
    public Gender getToGender() {
        return getGender().equals("MALE") ? Gender.MALE : Gender.FEMALE;
    }

    public LocalDateTime convertToBirthLocalDateTime() {
        return getBirth() != null ? getBirth().atStartOfDay() : null;
    }

    public LocalDateTime convertToFirstMeetDateLocalDateTime() {
        return getFirstMeetDate() != null ? getFirstMeetDate().atStartOfDay() : null;
    }
}
