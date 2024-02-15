package com.ppp.common.client;

import com.ppp.domain.common.constant.VideoCompressType;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ppp.common.exception.ErrorCode.FILE_CLEAN_JOB_FAILED;


public interface VideoConvertClient {
    Logger log = LoggerFactory.getLogger(VideoConvertClient.class);
    String DEFAULT_PATH = "temp/encoded";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHH");

    String compress(MultipartFile file, VideoCompressType compressType);

    default void deleteTempVideoCreatedBefore(int hour) {
        try {
            Path targetPath = Path.of(DEFAULT_PATH + "/" + LocalDateTime.now().minusHours(hour).format(dateTimeFormatter));
            List<File> directoriesToBeDeleted = Files.walk(Path.of(DEFAULT_PATH), 1)
                    .filter(path -> path.compareTo(targetPath) <= 0)
                    .map(Path::toFile)
                    .toList();
            for (File dir : directoriesToBeDeleted)
                FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            log.error("Class : {}, Code : {}, Message : {}", getClass(), FILE_CLEAN_JOB_FAILED.getCode(), FILE_CLEAN_JOB_FAILED.getMessage());
        }
    }
}
