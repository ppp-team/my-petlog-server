package com.ppp.api.log.service;

import com.ppp.api.log.dto.request.LogRequest;
import com.ppp.api.log.dto.response.LogDetailResponse;
import com.ppp.api.log.dto.response.LogGroupByDateResponse;
import com.ppp.api.log.dto.response.LogResponse;
import com.ppp.api.log.exception.LogException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.api.user.exception.ErrorCode;
import com.ppp.api.user.exception.UserException;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.log.Log;
import com.ppp.domain.log.LogLocation;
import com.ppp.domain.log.constant.LogType;
import com.ppp.domain.log.repository.LogRepository;
import com.ppp.domain.pet.Pet;
import com.ppp.domain.pet.repository.PetRepository;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.ppp.api.log.exception.ErrorCode.*;
import static com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND;
import static com.ppp.domain.log.constant.LogLocationType.CUSTOM;
import static com.ppp.domain.log.constant.LogLocationType.KAKAO;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
    private final PetRepository petRepository;
    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createLog(User user, Long petId, LogRequest request) {
        Pet pet = petRepository.findByIdAndIsDeletedFalse(petId)
                .orElseThrow(() -> new PetException(PET_NOT_FOUND));
        User mangerUser = userRepository.findByIdAndIsDeletedFalse(request.getManagerId())
                .filter(maybeUser -> guardianRepository.existsByUserIdAndPetId(maybeUser.getId(), petId))
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        validateAccessLog(petId, user);

        Log log = Log.builder()
                .datetime(LocalDateTime.parse(request.getDatetime()))
                .typeMap(getTypeMap(request))
                .memo(request.getMemo())
                .isImportant(request.getIsImportant())
                .isComplete(request.getIsComplete())
                .memo(request.getMemo())
                .pet(pet)
                .manager(mangerUser)
                .build();
        log.addLocation(getLocationIfExists(request, log));
        logRepository.save(log);
    }

    private LogLocation getLocationIfExists(LogRequest request, Log log) {
        if (!LogType.WALK.name().equals(request.getType()))
            return null;
        if (request.getIsCustomLocation())
            return LogLocation.builder()
                    .type(CUSTOM)
                    .log(log)
                    .build();
        else if (request.getKakaoLocationId() == null) {
            throw new LogException(LOCATION_INCORRECT);
        }
        return LogLocation.builder()
                .type(KAKAO)
                .mapId(request.getKakaoLocationId())
                .log(log)
                .build();
    }

    private Map<String, String> getTypeMap(LogRequest request) {
        Map<String, String> typeMap = new HashMap<>();
        LogType type = LogType.valueOf(request.getType());
        typeMap.put("type", type.name());
        if (request.getSubType() != null && !request.getSubType().isBlank()) {
            typeMap.put("subType", request.getSubType());
        }
        return typeMap;
    }


    @Transactional
    public void updateLog(User user, Long petId, Long logId, LogRequest request) {
        Log log = logRepository.findByIdAndIsDeletedFalse(logId)
                .orElseThrow(() -> new LogException(LOG_NOT_FOUND));
        User mangerUser = userRepository.findByIdAndIsDeletedFalse(request.getManagerId())
                .filter(maybeUser -> guardianRepository.existsByUserIdAndPetId(maybeUser.getId(), petId))
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        validateAccessLog(petId, user);

        log.update(LocalDateTime.parse(request.getDatetime()), getTypeMap(request), request.getMemo(),
                request.getIsImportant(), request.getIsComplete(), mangerUser, getLocationIfExists(request, log));
    }

    @Transactional
    public void deleteLog(User user, Long petId, Long logId) {
        Log log = logRepository.findByIdAndIsDeletedFalse(logId)
                .orElseThrow(() -> new LogException(LOG_NOT_FOUND));
        validateAccessLog(petId, user);

        log.delete();
    }

    private void validateAccessLog(Long petId, User user) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), petId))
            throw new LogException(FORBIDDEN_PET_SPACE);
    }

    public LogDetailResponse displayLog(User user, Long petId, Long logId) {
        Log log = logRepository.findByIdAndIsDeletedFalse(logId)
                .orElseThrow(() -> new LogException(LOG_NOT_FOUND));
        validateAccessLog(petId, user);

        return LogDetailResponse.from(log);
    }

    public LogGroupByDateResponse displayLogsByDate(User user, Long petId, int year, int month, int day) {
        LocalDateTime startDateTime;
        try {
            startDateTime = LocalDate.of(year, month, day).atStartOfDay();
        } catch (DateTimeException e) {
            throw new LogException(INVALID_DATE);
        }
        validateAccessLog(petId, user);

        LocalDateTime endDateTime = startDateTime.plusDays(1).minusNanos(1);
        return LogGroupByDateResponse.of(startDateTime,
                logRepository.findByPetIdAndAndDatetimeBetweenAndIsDeletedFalse(petId, startDateTime, endDateTime)
                        .stream().map(log -> LogResponse.from(log, user.getId()))
                        .collect(Collectors.toList()));
    }

    public Slice<LogGroupByDateResponse> displayLogsToDo(User user, Long petId, int page, int size) {
        validateAccessLog(petId, user);

        LocalDateTime today = LocalDate.now().atStartOfDay();
        return getGroupedLogsSlice(
                logRepository.findByPetIdAndAndDatetimeAfterAndIsDeletedFalse(petId,
                        today, PageRequest.of(page, size)), user.getId());
    }

    private Slice<LogGroupByDateResponse> getGroupedLogsSlice(Slice<Log> logSlice, String userId) {
        if (logSlice.getContent().isEmpty())
            return new SliceImpl<>(new ArrayList<>(), logSlice.getPageable(), logSlice.hasNext());

        List<LogGroupByDateResponse> content = new ArrayList<>();
        List<LogResponse> sameDaysLogs = new ArrayList<>();
        LocalDateTime prevDate = logSlice.getContent().get(0).getDatetime();
        for (Log log : logSlice.getContent()) {
            LocalDateTime currentDate = log.getDatetime();
            if (!Objects.equals(prevDate.toLocalDate(), currentDate.toLocalDate())) {
                content.add(LogGroupByDateResponse.of(prevDate, sameDaysLogs));
                prevDate = currentDate;
                sameDaysLogs = new ArrayList<>();
            }
            sameDaysLogs.add(LogResponse.from(log, userId));
        }
        content.add(LogGroupByDateResponse.of(prevDate, sameDaysLogs));

        return new SliceImpl<>(content, logSlice.getPageable(), logSlice.hasNext());
    }

    @Transactional
    public void checkComplete(User user, Long petId, Long logId) {
        Log log = logRepository.findByIdAndIsDeletedFalse(logId)
                .orElseThrow(() -> new LogException(LOG_NOT_FOUND));
        validateAccessLog(petId, user);

        log.switchIsComplete();
    }
}
