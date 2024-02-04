package com.ppp.api.config;

import com.ppp.ApiApplication;
import com.ppp.common.config.JasyptConfig;
import com.ppp.domain.guardian.Guardian;
import com.ppp.domain.guardian.constant.GuardianRole;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.ppp.domain.common.constant.CacheValue.PET_SPACE_AUTHORITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ApiApplication.class)
public class CacheIntegrationTest {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuardianRepository guardianRepository;

    @MockBean(JasyptConfig.class)
    private JasyptConfig jasyptConfig;

    User userA = User.builder()
            .id("abcd")
            .username("abcde")
            .email("abcde@gmail.com")
            .build();

    Pet pet = Pet.builder()
            .id(1L)
            .user(userA)
            .build();

    Guardian guardian = new Guardian(GuardianRole.MEMBER, pet, userA);


    @BeforeEach
    void setUp() {
        userRepository.save(userA);
        petRepository.save(pet);
        guardianRepository.save(guardian);
    }

    @AfterEach
    void tearDown() {
        Objects.requireNonNull(cacheManager.getCache(PET_SPACE_AUTHORITY.getValue())).evictIfPresent("abcd,1");
    }

    @Test
    @DisplayName("캐싱 성공")
    void cache_success() {
        //given
        //when
        boolean cacheMiss = guardianRepository.existsByUserIdAndPetId(userA.getId(), pet.getId());
        Boolean cached = cacheManager.getCache(PET_SPACE_AUTHORITY.getValue()).get("abcd,1", Boolean.class);
        //then
        assertTrue(cacheMiss);
        assertEquals(Boolean.TRUE, cached);
    }
}
