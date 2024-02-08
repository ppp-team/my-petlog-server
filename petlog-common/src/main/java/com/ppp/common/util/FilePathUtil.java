package com.ppp.common.util;

import com.ppp.domain.common.constant.Domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public class FilePathUtil {
    public static String createFileName(String fileName) {
        return UUID.randomUUID().toString().replaceAll("-", "") +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) +
                fileName.substring(fileName.lastIndexOf("."));
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
