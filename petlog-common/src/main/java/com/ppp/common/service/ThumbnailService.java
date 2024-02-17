package com.ppp.common.service;

import com.ppp.common.client.ThumbnailExtractClient;
import com.ppp.common.exception.ErrorCode;
import com.ppp.common.exception.FileException;
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
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ThumbnailService {
    @Value("${storage.uri}")
    private String storageUri;
    private final ThumbnailExtractClient thumbnailExtractClient;
    private final FileStorageManageService fileStorageManageService;


    public String uploadThumbnailFromStorageFile(String path, FileType type, Domain domain) throws Exception {
        File input = File.createTempFile("temp", type.getExtension());
        InputStream inputStream = new URL(storageUri + path).openStream();
        FileUtils.copyInputStreamToFile(inputStream, input);
        File thumbnailFile = thumbnailExtractClient.extractThumbnail(input, type);
        Optional<String> maybeThumbnail = fileStorageManageService.uploadImage(thumbnailFile, domain);
        thumbnailFile.delete();
        if (maybeThumbnail.isEmpty())
            throw new FileException(ErrorCode.THUMBNAIL_UPLOAD_FAILED);
        return maybeThumbnail.get();
    }
}
