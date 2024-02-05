package com.ppp.api.log.service;

import com.ppp.api.log.dto.request.LogRequest;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        if (request.getSubType() != null && !request.getSubType().isEmpty()) {
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
}
