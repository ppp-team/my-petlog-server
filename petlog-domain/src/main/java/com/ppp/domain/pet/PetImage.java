package com.ppp.domain.pet;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "pet_image")
public class PetImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private String url;

    private String thumbnailUrl;

    @Builder
    public PetImage(Pet pet, String url, String thumbnailUrl) {
        this.pet = pet;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }
}