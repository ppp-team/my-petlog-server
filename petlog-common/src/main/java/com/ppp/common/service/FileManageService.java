package com.ppp.common.service;

import com.ppp.common.client.FileStorageClient;
import com.ppp.common.util.FilePathUtil;
import com.ppp.domain.common.constant.Domain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileManageService {
    private final FileStorageClient fileStorageClient;
    public static final List<String> ALLOW_IMAGE_CODES = List.of(".jpeg", ".png", ".jpg", ".gif");

    public Optional<String> uploadImage(MultipartFile multipartFile, Domain domain) {
        Optional<String> maybeExtension = FilePathUtil.getFileExtension(
                Objects.requireNonNull(multipartFile.getOriginalFilename()));
        if (maybeExtension.isEmpty() || !ALLOW_IMAGE_CODES.contains(maybeExtension.get()))
            return Optional.empty();
        return Optional.of(fileStorageClient.upload(multipartFile, domain));
    }

    public List<String> uploadImages(List<MultipartFile> multipartFiles, Domain domain) {
        return multipartFiles.stream()
                .filter(multipartFile -> {
                    Optional<String> maybeExtension = FilePathUtil.getFileExtension(
                                    Objects.requireNonNull(multipartFile.getOriginalFilename()));
                    return maybeExtension.isPresent() && ALLOW_IMAGE_CODES.contains(maybeExtension.get());})
                .map(multipartFile -> fileStorageClient.upload(multipartFile, domain))
                .collect(Collectors.toList());
    }

    public void deleteImage(String imagePath) {
        fileStorageClient.delete(imagePath);
    }

    public void deleteImages(List<String> imagePaths) {
        imagePaths.forEach(fileStorageClient::delete);
    }
}
