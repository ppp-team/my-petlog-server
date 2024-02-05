package com.ppp.api.log.service;

import com.ppp.api.log.dto.request.LogRequest;
import com.ppp.api.log.exception.LogException;
import com.ppp.api.pet.exception.PetException;
import com.ppp.api.user.exception.ErrorCode;
import com.ppp.api.user.exception.UserException;
import com.ppp.domain.guardian.repository.GuardianRepository;
import com.ppp.domain.log.Log;
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

import static com.ppp.api.log.exception.ErrorCode.FORBIDDEN_PET_SPACE;
import static com.ppp.api.log.exception.ErrorCode.LOG_NOT_FOUND;
import static com.ppp.api.pet.exception.ErrorCode.PET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
    private final PetRepository petRepository;
    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;

    public void createLog(User user, Long petId, LogRequest request) {
        Pet pet = petRepository.findByIdAndIsDeletedFalse(petId)
                .orElseThrow(() -> new PetException(PET_NOT_FOUND));
        User mangerUser = userRepository.findByIdAndIsDeletedFalse(request.getManagerId())
                .filter(maybeUser -> guardianRepository.existsByUserIdAndPetId(maybeUser.getId(), petId))
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        validateAccessLog(petId, user);

        logRepository.save(Log.builder()
                .datetime(LocalDateTime.parse(request.getDatetime()))
                .type(LogType.valueOf(request.getType()))
                .subType(request.getSubType())
                .memo(request.getMemo())
                .isImportant(request.isImportant())
                .isComplete(request.isComplete())
                .memo(request.getMemo())
                .pet(pet)
                .manager(mangerUser)
                .build());
    }


    private void validateAccessLog(Long petId, User user) {
        if (!guardianRepository.existsByUserIdAndPetId(user.getId(), petId))
            throw new LogException(FORBIDDEN_PET_SPACE);
    }

    @Transactional
    public void updateLog(User user, Long petId, Long logId, LogRequest request) {
        Log log = logRepository.findByIdAndIsDeletedFalse(logId)
                .orElseThrow(() -> new LogException(LOG_NOT_FOUND));
        User mangerUser = userRepository.findByIdAndIsDeletedFalse(request.getManagerId())
                .filter(maybeUser -> guardianRepository.existsByUserIdAndPetId(maybeUser.getId(), petId))
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        validateAccessLog(petId, user);

        log.update(LocalDateTime.parse(request.getDatetime()), LogType.valueOf(request.getType()), request.getSubType(),
                request.getMemo(), request.isImportant(), request.isComplete(), mangerUser);
    }

    @Transactional
    public void deleteLog(User user, Long petId, Long logId) {
        Log log = logRepository.findByIdAndIsDeletedFalse(logId)
                .orElseThrow(() -> new LogException(LOG_NOT_FOUND));
        validateAccessLog(petId, user);

        log.delete();
    }
}
