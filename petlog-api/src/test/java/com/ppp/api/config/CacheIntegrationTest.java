package com.ppp.api.config;

import com.ppp.ApiApplication;
import com.ppp.common.client.FfmpegClient;
import com.ppp.common.config.FfmpegConfig;
import com.ppp.common.config.JasyptConfig;
import com.ppp.common.service.CacheManageService;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.ppp.domain.common.constant.CacheValue.PET_SPACE_AUTHORITY;
import static org.junit.jupiter.api.Assertions.*;

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

    @Autowired
    private CacheManageService cacheManageService;

    @MockBean
    private JasyptConfig jasyptConfig;

    @MockBean
    private FfmpegConfig ffmpegConfig;

    @MockBean
    private FfmpegClient ffmpegClient;

    @MockBean
    private EmailConfig emailConfig;

    @MockBean
    private JavaMailSender javaMailSender;

    User userA = User.builder()
            .id("abcd")
            .nickname("abcde")
            .email("abcde@gmail.com")
            .build();

    User userB = User.builder()
            .id("qwerty")
            .nickname("qwerty")
            .email("qwerty@gmail.com")
            .build();

    Pet pet = Pet.builder()
            .id(1L)
            .user(userA)
            .build();

    Guardian guardianA = Guardian.builder()
            .guardianRole(GuardianRole.MEMBER)
            .pet(pet)
            .user(userA).build();


    @BeforeEach
    void setUp() {
        userRepository.save(userA);
        userRepository.save(userB);
        petRepository.save(pet);
        guardianRepository.save(guardianA);
    }

    @AfterEach
    void tearDown() {
        Objects.requireNonNull(cacheManager.getCache(PET_SPACE_AUTHORITY.getValue())).evictIfPresent("abcd,1");
        Objects.requireNonNull(cacheManager.getCache(PET_SPACE_AUTHORITY.getValue())).evictIfPresent("qwerty,1");
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

    @Test
    @DisplayName("캐싱 실패-when result is false")
    void cache_fail() {
        //given
        //when
        boolean cacheMiss = guardianRepository.existsByUserIdAndPetId(userB.getId(), pet.getId());
        Boolean cached = cacheManager.getCache(PET_SPACE_AUTHORITY.getValue()).get("qwerty,1", Boolean.class);
        //then
        assertFalse(cacheMiss);
        assertNull(cached);
    }

    @Test
    @DisplayName("캐싱 데이터 삭제 성공")
    void deleteCached_success() {
        //given
        cacheManager.getCache(PET_SPACE_AUTHORITY.getValue()).put("abcd,1", true);
        Boolean cached = cacheManager.getCache(PET_SPACE_AUTHORITY.getValue()).get("abcd,1", Boolean.class);
        assert cached != null && cached;
        cacheManageService.deleteCachedPetSpaceAuthority(userA.getId(), 1L);
        Boolean deletedCache = cacheManager.getCache(PET_SPACE_AUTHORITY.getValue()).get("abcd,1", Boolean.class);
        //when
        //then
        assertNull(deletedCache);
    }
}
