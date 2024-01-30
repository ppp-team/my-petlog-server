package com.ppp.common.util;

import com.ppp.common.exception.ErrorCode;
import com.ppp.common.exception.FileException;
import com.ppp.domain.common.constant.FileDomain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FilePathUtil {
    public static String createFileName(String fileName) {
        return UUID.randomUUID().toString().replaceAll("-", "") +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) +
                getFileExtension(fileName);
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0)
            throw new FileException(ErrorCode.NOT_VALID_EXTENSION);
        return fileName.substring(dotIndex);
    }

    public static String createFilePath(FileDomain fileDomain) {
        return fileDomain.name() + "/" + LocalDate.now() + "/";
    }

    private FilePathUtil() {
    }
}
