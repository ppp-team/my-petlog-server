package com.ppp.common.util;

import com.ppp.domain.common.constant.Domain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.ppp.domain.common.constant.Domain.DIARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilePathUtilTest {
    @Test
    @DisplayName("파일명 생성-성공")
    void createFileName_success() {
        //given
        String originalName = "songsong.jpg";
        //when
        String generatedName = FilePathUtil.getFileNameAndExtension(originalName);
        //then
        assertEquals(32 + "yyyyMMddHHmmssSSS".length() + ".jpg".length(), generatedName.length());
    }

    @Test
    @DisplayName("확장자 가져오기-성공")
    void getFileExtension_success() {
        //given
        String fileName = "songsong.jpg";
        //when
        Optional<String> maybeString = FilePathUtil.getFileExtension(fileName);
        //then
        assertEquals(".jpg", maybeString.get());
    }

    @Test
    @DisplayName("확장자 가져오기-실패-not valid extension")
    void getFileExtension_fail_NOT_VALID_EXTENSION() {
        //given
        String fileName = "songsong";
        //when
        Optional<String> maybeString = FilePathUtil.getFileExtension(fileName);
        //then
        assertTrue(maybeString.isEmpty());
    }

    @Test
    @DisplayName("파일 경로 생성-성공")
    void createFilePath_success() {
        //given
        Domain domain = DIARY;
        //when
        String filePath = FilePathUtil.createFilePath(domain);
        //then
        assertTrue(filePath.startsWith(DIARY.name()));
        assertEquals(DIARY.name().length() + "yyyy-MM-dd".length() + 2, filePath.length());
    }

}