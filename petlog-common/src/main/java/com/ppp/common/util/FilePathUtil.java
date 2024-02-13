package com.ppp.common.util;

import com.ppp.domain.common.constant.Domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public class FilePathUtil {
    public static String getFileNameAndExtension(String fileName) {
        return createFileName() + getFileExtension(fileName).orElse(".jpg");
    }

    public static String createFileName() {
        return UUID.randomUUID().toString().replaceAll("-", "") +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    public static Optional<String> getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0)
            return Optional.empty();
        return Optional.of(fileName.substring(dotIndex));
    }

    public static String createFilePath(Domain domain) {
        return domain.name() + "/" + LocalDate.now() + "/";
    }

    private FilePathUtil() {
    }
}
