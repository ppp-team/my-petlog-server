package com.ppp.common.client;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ThumbnailExtractClient {
    File extractThumbnail(String path);

    File extractThumbnail(MultipartFile multipartFile);
}
