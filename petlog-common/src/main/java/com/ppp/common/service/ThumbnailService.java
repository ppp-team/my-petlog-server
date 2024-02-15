package com.ppp.common.service;

import com.ppp.common.client.ThumbnailExtractClient;
import com.ppp.domain.common.constant.Domain;
import com.ppp.domain.common.constant.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.URL;


@Service
@Slf4j
@RequiredArgsConstructor
public class ThumbnailService {
    @Value("${storage.uri}")
    private String storageUri;
    private final ThumbnailExtractClient thumbnailExtractClient;
    private final FileStorageManageService fileStorageManageService;
    public static final String DEFAULT_THUMBNAIL_PATH = "RESOURCE/default-thumbnail.png";

    public String uploadThumbnailFromStorageFile(String path, FileType type, Domain domain) {
        String uploadedThumbnailPath = DEFAULT_THUMBNAIL_PATH;
        try {
            File input = File.createTempFile("temp", type.getExtension());
            InputStream inputStream = new URL(storageUri + path).openStream();
            FileUtils.copyInputStreamToFile(inputStream, input);
            File thumbnailFile = thumbnailExtractClient.extractThumbnail(input, type);
            uploadedThumbnailPath = fileStorageManageService.uploadImage(thumbnailFile, domain)
                    .orElse(DEFAULT_THUMBNAIL_PATH);
            thumbnailFile.delete();
        } catch (Exception e) {
        }
        return uploadedThumbnailPath;
    }
}
