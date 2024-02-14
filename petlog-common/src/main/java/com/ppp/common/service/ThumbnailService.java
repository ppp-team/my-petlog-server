package com.ppp.common.service;

import com.ppp.common.client.ThumbnailExtractClient;
import com.ppp.domain.common.constant.Domain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


@Service
@Slf4j
@RequiredArgsConstructor
public class ThumbnailService {
    private final ThumbnailExtractClient thumbnailExtractClient;
    private final FileStorageManageService fileStorageManageService;
    public static final String DEFAULT_THUMBNAIL_PATH = "RESOURCE/default-thumbnail.png";

    public String uploadThumbnail(MultipartFile multipartFile, Domain domain) {
        File thumbnailFile = thumbnailExtractClient.extractThumbnail(multipartFile);
        String uploadedThumbnailPath = fileStorageManageService.uploadImage(thumbnailFile, domain)
                .orElse(DEFAULT_THUMBNAIL_PATH);
        thumbnailFile.delete();
        return uploadedThumbnailPath;
    }

    public String uploadThumbnail(String filePath, Domain domain) {
        File thumbnailFile = thumbnailExtractClient.extractThumbnail(filePath);
        String uploadedThumbnailPath = fileStorageManageService.uploadImage(thumbnailFile, domain)
                .orElse(DEFAULT_THUMBNAIL_PATH);
        thumbnailFile.delete();
        return uploadedThumbnailPath;
    }
}
