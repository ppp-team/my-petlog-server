package com.ppp.api.log.service;

import com.ppp.api.log.dto.request.LogRequest;
import com.ppp.api.log.exception.LogException;
import com.ppp.api.pet.exception.ErrorCode;
import com.ppp.api.pet.exception.PetException;
import com.ppp.api.user.exception.UserException;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.log.Log;
import com.ppp.domain.log.constant.LogLocationType;
import com.ppp.domain.log.repository.LogRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static com.ppp.api.log.exception.ErrorCode.*;
import static com.ppp.api.user.exception.ErrorCode.NOT_FOUND_USER;
import static com.ppp.domain.log.constant.LogType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {
    @Mock
    private LogRepository logRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private GuardianRepository guardianRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private LogService logService;

    User user = User.builder()
            .id("abcde1234")
            .nickname("hi")
            .build();
    User userA = User.builder()
            .id("abc123")
            .nickname("첫째누나")
            .build();
    Pet pet = Pet.builder()
            .id(1L).build();

    @Test
    @DisplayName("건강 기록 생성 성공")
    void createLog_success() {
        //given
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(true)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.of(userA));
        given(guardianRepository.existsByUserIdAndPetId("abc123", 1L))
                .willReturn(true);
        given(guardianRepository.existsByUserIdAndPetId("abcde1234", 1L))
                .willReturn(true);
        //when
        logService.createLog(user, 1L, request);
        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        //then
        verify(logRepository, times(1)).save(captor.capture());
        assertEquals(FEED.name(), captor.getValue().getTypeMap().get("type"));
        assertEquals(LocalDateTime.of(2024, 1, 1, 11, 11), captor.getValue().getDatetime());
        assertEquals("습식", captor.getValue().getTypeMap().get("subType"));
        assertTrue(captor.getValue().isComplete());
        assertFalse(captor.getValue().isImportant());
        assertEquals("로얄 캐닌 연어맛 500g 줬음!", captor.getValue().getMemo());
        assertEquals(1L, captor.getValue().getPet().getId());
        assertEquals("abc123", captor.getValue().getManager().getId());
    }

    @Test
    @DisplayName("건강 기록 생성 성공-직접 입력 로케이션")
    void createLog_success_WhenCustomLocation() {
        //given
        LogRequest request = LogRequest.builder()
                .type("WALK")
                .subType("스타벅스 합정점")
                .isCustomLocation(true)
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isComplete(true)
                .isImportant(false)
                .memo("합정점에 잠깐 들려 커피 사기")
                .managerId("abc123")
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.of(userA));
        given(guardianRepository.existsByUserIdAndPetId("abc123", 1L))
                .willReturn(true);
        given(guardianRepository.existsByUserIdAndPetId("abcde1234", 1L))
                .willReturn(true);
        //when
        logService.createLog(user, 1L, request);
        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        //then
        verify(logRepository, times(1)).save(captor.capture());
        assertEquals(WALK.name(), captor.getValue().getTypeMap().get("type"));
        assertEquals(LocalDateTime.of(2024, 1, 1, 11, 11), captor.getValue().getDatetime());
        assertEquals("스타벅스 합정점", captor.getValue().getTypeMap().get("subType"));
        assertTrue(captor.getValue().isComplete());
        assertFalse(captor.getValue().isImportant());
        assertEquals("합정점에 잠깐 들려 커피 사기", captor.getValue().getMemo());
        assertEquals(1L, captor.getValue().getPet().getId());
        assertEquals("abc123", captor.getValue().getManager().getId());
        assertNotNull(captor.getValue().getLocation());
        assertEquals(LogLocationType.CUSTOM, captor.getValue().getLocation().getType());
    }

    @Test
    @DisplayName("건강 기록 생성 성공-카카오 로케이션")
    void createLog_success_WhenKakaoMapLocation() {
        //given
        LogRequest request = LogRequest.builder()
                .type("WALK")
                .subType("스타벅스 합정점")
                .kakaoLocationId(2057327896L)
                .isCustomLocation(false)
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isComplete(true)
                .isImportant(false)
                .memo("합정점에 잠깐 들려 커피 사기")
                .managerId("abc123")
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.of(userA));
        given(guardianRepository.existsByUserIdAndPetId("abc123", 1L))
                .willReturn(true);
        given(guardianRepository.existsByUserIdAndPetId("abcde1234", 1L))
                .willReturn(true);
        //when
        logService.createLog(user, 1L, request);
        ArgumentCaptor<Log> captor = ArgumentCaptor.forClass(Log.class);
        //then
        verify(logRepository, times(1)).save(captor.capture());
        assertEquals(WALK.name(), captor.getValue().getTypeMap().get("type"));
        assertEquals(LocalDateTime.of(2024, 1, 1, 11, 11), captor.getValue().getDatetime());
        assertEquals("스타벅스 합정점", captor.getValue().getTypeMap().get("subType"));
        assertTrue(captor.getValue().isComplete());
        assertFalse(captor.getValue().isImportant());
        assertEquals("합정점에 잠깐 들려 커피 사기", captor.getValue().getMemo());
        assertEquals(1L, captor.getValue().getPet().getId());
        assertEquals("abc123", captor.getValue().getManager().getId());
        assertNotNull(captor.getValue().getLocation());
        assertEquals(LogLocationType.KAKAO, captor.getValue().getLocation().getType());
        assertEquals(2057327896L, captor.getValue().getLocation().getMapId());
    }

    @Test
    @DisplayName("건강 기록 생성 실패-카카오 로케이션-location incorrect")
    void createLog_fail_WhenKakaoMapLocation_LOCATION_INCORRECT() {
        //given
        LogRequest request = LogRequest.builder()
                .type("WALK")
                .subType("스타벅스 합정점")
                .kakaoLocationId(null)
                .isCustomLocation(false)
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isComplete(true)
                .isImportant(false)
                .memo("합정점에 잠깐 들려 커피 사기")
                .managerId("abc123")
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.of(userA));
        given(guardianRepository.existsByUserIdAndPetId("abc123", 1L))
                .willReturn(true);
        given(guardianRepository.existsByUserIdAndPetId("abcde1234", 1L))
                .willReturn(true);
        //when
        LogException exception = assertThrows(LogException.class, () -> logService.createLog(user, 1L, request));
        //then
        assertEquals(LOCATION_INCORRECT.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 생성 실패-pet not found")
    void createLog_fail_PET_NOT_FOUND() {
        //given
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(false)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        PetException exception = assertThrows(PetException.class, () -> logService.createLog(user, 1L, request));
        //then
        assertEquals(ErrorCode.PET_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 생성 실패-not found user")
    void createLog_fail_NOT_FOUND_USER() {
        //given
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(false)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.empty());
        //when
        UserException exception = assertThrows(UserException.class, () -> logService.createLog(user, 1L, request));
        //then
        assertEquals(NOT_FOUND_USER.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 생성 실패-not found user-manager is not guardian")
    void createLog_fail_NOT_FOUND_USER_WhenManagerIsNotGuardian() {
        //given
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(false)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.of(userA));
        given(guardianRepository.existsByUserIdAndPetId("abc123", 1L))
                .willReturn(false);
        //when
        UserException exception = assertThrows(UserException.class, () -> logService.createLog(user, 1L, request));
        //then
        assertEquals(NOT_FOUND_USER.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 생성 실패-forbidden pet space")
    void createLog_fail_FORBIDDEN_PET_SPACE() {
        //given
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(false)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(petRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(pet));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.of(userA));
        given(guardianRepository.existsByUserIdAndPetId("abc123", 1L))
                .willReturn(true);
        given(guardianRepository.existsByUserIdAndPetId("abcde1234", 1L))
                .willReturn(false);
        //when
        LogException exception = assertThrows(LogException.class, () -> logService.createLog(user, 1L, request));
        //then
        assertEquals(FORBIDDEN_PET_SPACE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 수정 성공")
    void updateLog_success() {
        //given
        Log log = Log.builder()
                .typeMap(Map.of("type", CUSTOM.name(),
                        "subType", "강아지 카페"))
                .datetime(LocalDateTime.of(2024, 2, 2, 22, 22))
                .isImportant(true)
                .isComplete(false)
                .memo("고구마 챙겨가기")
                .manager(user)
                .pet(pet)
                .build();
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(true)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(logRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(log));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.of(userA));
        given(guardianRepository.existsByUserIdAndPetId("abc123", 1L))
                .willReturn(true);
        given(guardianRepository.existsByUserIdAndPetId("abcde1234", 1L))
                .willReturn(true);
        //when
        logService.updateLog(user, 1L, 1L, request);
        //then
        assertEquals(FEED.name(), log.getTypeMap().get("type"));
        assertEquals(LocalDateTime.of(2024, 1, 1, 11, 11), log.getDatetime());
        assertEquals("습식", log.getTypeMap().get("subType"));
        assertTrue(log.isComplete());
        assertFalse(log.isImportant());
        assertEquals("로얄 캐닌 연어맛 500g 줬음!", log.getMemo());
        assertEquals("abc123", log.getManager().getId());
    }

    @Test
    @DisplayName("건강 기록 수정 실패-log not found")
    void updateLog_fail_LOG_NOT_FOUND() {
        //given
        Log log = Log.builder()
                .typeMap(Map.of("type", CUSTOM.name(),
                        "subType", "강아지 카페"))
                .datetime(LocalDateTime.of(2024, 2, 2, 22, 22))
                .isImportant(true)
                .isComplete(false)
                .memo("고구마 챙겨가기")
                .manager(user)
                .pet(pet)
                .build();
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(false)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(logRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        LogException exception = assertThrows(LogException.class, () -> logService.updateLog(user, 1L, 1L, request));
        //then
        assertEquals(LOG_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 수정 실패-not found user")
    void updateLog_fail_NOT_FOUND_USER() {
        //given
        Log log = Log.builder()
                .typeMap(Map.of("type", CUSTOM.name(),
                        "subtype", "강아지 카페"))
                .datetime(LocalDateTime.of(2024, 2, 2, 22, 22))
                .isImportant(true)
                .isComplete(false)
                .memo("고구마 챙겨가기")
                .manager(user)
                .pet(pet)
                .build();
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(false)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(logRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(log));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.empty());
        //when
        UserException exception = assertThrows(UserException.class, () -> logService.updateLog(user, 1L, 1L, request));
        //then
        assertEquals(NOT_FOUND_USER.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 수정 실패-not found user-when manager id not guardian")
    void updateLog_fail_NOT_FOUND_USER_WhenManagerIsNotGuardian() {
        //given
        Log log = Log.builder()
                .typeMap(Map.of("type", CUSTOM.name(),
                        "subType", "강아지 카페"))
                .datetime(LocalDateTime.of(2024, 2, 2, 22, 22))
                .isImportant(true)
                .isComplete(false)
                .memo("고구마 챙겨가기")
                .manager(user)
                .pet(pet)
                .build();
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(false)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(logRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(log));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.of(userA));
        given(guardianRepository.existsByUserIdAndPetId("abc123", 1L))
                .willReturn(false);
        //when
        UserException exception = assertThrows(UserException.class, () -> logService.updateLog(user, 1L, 1L, request));
        //then
        assertEquals(NOT_FOUND_USER.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 수정 실패-forbidden pet space")
    void updateLog_fail_FORBIDDEN_PET_SPACE() {
        //given
        Log log = Log.builder()
                .typeMap(Map.of("type", CUSTOM.name(),
                        "subtype", "강아지 카페"))
                .datetime(LocalDateTime.of(2024, 2, 2, 22, 22))
                .isImportant(true)
                .isComplete(false)
                .memo("고구마 챙겨가기")
                .manager(user)
                .pet(pet)
                .build();
        LogRequest request = LogRequest.builder()
                .type("FEED")
                .subType("습식")
                .datetime(LocalDateTime.of(2024, 1, 1, 11, 11).toString())
                .isCustomLocation(false)
                .isComplete(false)
                .isImportant(false)
                .memo("로얄 캐닌 연어맛 500g 줬음!")
                .managerId("abc123")
                .build();
        given(logRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(log));
        given(userRepository.findByIdAndIsDeletedFalse("abc123"))
                .willReturn(Optional.of(userA));
        given(guardianRepository.existsByUserIdAndPetId("abc123", 1L))
                .willReturn(true);
        given(guardianRepository.existsByUserIdAndPetId("abcde1234", 1L))
                .willReturn(false);
        //when
        LogException exception = assertThrows(LogException.class, () -> logService.updateLog(user, 1L, 1L, request));
        //then
        assertEquals(FORBIDDEN_PET_SPACE.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 삭제 성공")
    void deleteLog_success() {
        //given
        Log log = Log.builder()
                .typeMap(Map.of("type", CUSTOM.name(),
                        "subtype", "강아지 카페"))
                .datetime(LocalDateTime.of(2024, 2, 2, 22, 22))
                .isImportant(true)
                .isComplete(false)
                .memo("고구마 챙겨가기")
                .manager(user)
                .pet(pet)
                .build();
        given(logRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(log));
        given(guardianRepository.existsByUserIdAndPetId("abcde1234", 1L))
                .willReturn(true);
        //when
        logService.deleteLog(user, 1L, 1L);
        //then
        assertTrue(log.isDeleted());
        assertNull(log.getLocation());
    }

    @Test
    @DisplayName("건강 기록 삭제 실패-log not found")
    void deleteLog_fail_LOG_NOT_FOUND() {
        //given
        given(logRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.empty());
        //when
        LogException exception = assertThrows(LogException.class, () -> logService.deleteLog(user, 1L, 1L));
        //then
        assertEquals(LOG_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("건강 기록 삭제 실패-forbidden pet space")
    void deleteLog_fail_FORBIDDEN_PET_SPACE() {
        //given
        Log log = Log.builder()
                .typeMap(Map.of("type", CUSTOM.name(),
                        "subtype", "강아지 카페"))
                .datetime(LocalDateTime.of(2024, 2, 2, 22, 22))
                .isImportant(true)
                .isComplete(false)
                .memo("고구마 챙겨가기")
                .manager(user)
                .pet(pet)
                .build();
        given(logRepository.findByIdAndIsDeletedFalse(anyLong()))
                .willReturn(Optional.of(log));
        given(guardianRepository.existsByUserIdAndPetId(anyString(), anyLong()))
                .willReturn(false);
        //when
        LogException exception = assertThrows(LogException.class, () -> logService.deleteLog(user, 1L, 1L));
        //then
        assertEquals(FORBIDDEN_PET_SPACE.getCode(), exception.getCode());
    }
}