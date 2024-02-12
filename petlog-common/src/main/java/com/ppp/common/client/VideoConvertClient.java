package com.ppp.common.client;

import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;


public interface VideoConvertClient {
    String resolution(MultipartFile file);

    void deleteDirectoryCreatedBeforeThreeHours();

    String DEFAULT_PATH = "temp/encoded";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHH");

}
