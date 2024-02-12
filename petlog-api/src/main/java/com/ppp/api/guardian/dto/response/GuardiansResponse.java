package com.ppp.api.guardian.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuardiansResponse {
    private int count;
    private List<GuardianResponse> data;
}
