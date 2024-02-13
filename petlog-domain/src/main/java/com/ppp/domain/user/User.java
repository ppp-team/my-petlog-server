package com.ppp.domain.user;


import com.ppp.domain.common.BaseTimeEntity;
import com.ppp.domain.common.GenerationUtil;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.user.constant.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @Column(length = 100, unique = true)
    private String id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private String username;

    @Column(columnDefinition = "BIT default 0")
    private Boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Pet> pets = new ArrayList<>();


    // 팩토리 메소드를 이용한 생성
    public static User createUserByEmail(String email) {
        User user = new User();
        user.id = GenerationUtil.generateIdFromEmail(email);
        return user;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
