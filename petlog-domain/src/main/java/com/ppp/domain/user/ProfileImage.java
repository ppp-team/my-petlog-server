package com.ppp.domain.user;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "profile_image")
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private String url;

    private String thumbnailUrl;

    @Builder
    public ProfileImage(User user, String url, String thumbnailUrl) {
        this.user = user;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }
}