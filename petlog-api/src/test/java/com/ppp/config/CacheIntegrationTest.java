package com.ppp.config;

import com.ppp.ApiApplication;
import com.ppp.domain.mock.Mock;
import com.ppp.domain.mock.constant.MockType;
import com.ppp.domain.mock.repository.MockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ApiApplication.class)
public class CacheIntegrationTest {
    @Autowired
    CacheManager cacheManager;

    @Autowired
    MockRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository.save(new Mock(1L, "songsong@gmail.com", "1234", MockType.USER));
        mockRepository.save(new Mock(2L, "foundation@email.com", "1234", MockType.USER));
    }

    @AfterEach
    void tearDown() {
        mockRepository.deleteAll();
        Objects.requireNonNull(cacheManager.getCache("mocks")).evictIfPresent("songsong@gmail.com");
        Objects.requireNonNull(cacheManager.getCache("mocks")).evictIfPresent("foundation@email.com");
    }

    @Test
    @DisplayName("캐싱 성공")
    void cache_success() {
        //given
        //when
        Optional<Mock> cacheMiss = mockRepository.findFirstByEmail("songsong@gmail.com");
        Optional<Mock> cachedMock = Optional.ofNullable(cacheManager.getCache("mocks"))
                .map(c -> c.get("songsong@gmail.com", Mock.class));
        //then
        assert cachedMock.isPresent();
        assert cacheMiss.isPresent();
        assertEquals(cacheMiss.get().getEmail(), cachedMock.get().getEmail());
    }

    @Test
    @DisplayName("캐시 필터링 성공")
    void cacheFiltered_success() {
        //given
        //when
        Optional<Mock> cacheMiss = mockRepository.findFirstByEmail("foundation@email.com");
        Optional<Mock> cachedMock = Optional.ofNullable(cacheManager.getCache("mocks"))
                .map(c -> c.get("foundation@email.com", Mock.class));
        //then
        assertTrue(cachedMock.isEmpty());
    }

}
