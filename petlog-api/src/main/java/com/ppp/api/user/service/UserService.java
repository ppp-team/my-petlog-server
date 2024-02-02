package com.ppp.api.user.service;

import com.ppp.api.user.exception.ErrorCode;
import com.ppp.api.user.exception.NotFoundUserException;
import com.ppp.api.user.exception.UserException;
import com.ppp.common.service.FileManageService;
import com.ppp.domain.common.constant.Domain;
import com.ppp.domain.user.ProfileImage;
import com.ppp.domain.user.User;
import com.ppp.domain.user.repository.ProfileImageRepository;
import com.ppp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final FileManageService fileManageService;
    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void createProfile(User user, MultipartFile profileImage, String nickname) {
        // save nickname
        User userFromDB = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
        userFromDB.setNickname(nickname);

        // save image
        if (profileImage != null && !profileImage.isEmpty()) {
            String path = uploadImageToS3(profileImage);

            createProfileImage(user, path);
        }
    }

    private String uploadImageToS3(MultipartFile profileImage) {
        return fileManageService.uploadImage(profileImage, Domain.USER)
                .orElseThrow(() -> new UserException(ErrorCode.PROFILE_REGISTRATION_FAILED));
    }

    private void createProfileImage(User user, String path) {
        Optional<ProfileImage> existingImage = profileImageRepository.findByUser(user);
        if (existingImage.isPresent()) {
            ProfileImage image = existingImage.get();
            image.setUrl(path);
        } else {
            ProfileImage newImage = ProfileImage.builder().user(user).url(path).build();
            profileImageRepository.save(newImage);
        }
    }
}
