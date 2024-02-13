package com.ppp.api.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyPetsResponse {
    private int count;
    private List<MyPetResponse> data;
}