package com.ppp.common.util;

import com.ppp.common.exception.ErrorCode;
import com.ppp.common.exception.FileException;
import com.ppp.domain.common.constant.FileDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.ppp.domain.common.constant.FileDomain.DIARY;
import static org.junit.jupiter.api.Assertions.*;

class FilePathUtilTest {
    @Test
    @DisplayName("파일명 생성-성공")
    void createFileName_success() {
        //given
        String originalName = "songsong.jpg";
        //when
        String generatedName = FilePathUtil.createFileName(originalName);
        //then
        assertEquals(32 + "yyyyMMddHHmmssSSS".length() + ".jpg".length(), generatedName.length());
    }

    @Test
    @DisplayName("확장자 가져오기-성공")
    void getFileExtension_success() {
        //given
        String fileName = "songsong.jpg";
        //when
        String extension = FilePathUtil.getFileExtension(fileName);
        //then
        assertEquals(".jpg", extension);
    }

    @Test
    @DisplayName("확장자 가져오기-실패-not valid extension")
    void getFileExtension_fail_NOT_VALID_EXTENSION() {
        //given
        String fileName = "songsong";
        //when
        FileException exception =
                assertThrows(FileException.class, () -> FilePathUtil.getFileExtension(fileName));
        //then
        assertEquals(exception.getCode(), ErrorCode.NOT_VALID_EXTENSION.name());
    }

    @Test
    @DisplayName("파일 경로 생성-성공")
    void createFilePath_success() {
        //given
        FileDomain fileDomain = DIARY;
        //when
        String filePath = FilePathUtil.createFilePath(fileDomain);
        //then
        assertTrue(filePath.startsWith(DIARY.name()));
        assertEquals(DIARY.name().length() + "yyyy-MM-dd".length() + 2, filePath.length());
    }

}