package com.ppp.common.service;

import com.ppp.common.client.ThumbnailExtractClient;
import com.ppp.domain.common.constant.Domain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThumbnailServiceTest {
    @Mock
    private ThumbnailExtractClient thumbnailExtractClient;
    @Mock
    private FileStorageManageService fileStorageManageService;
    @InjectMocks
    private ThumbnailService thumbnailService;

    @Test
    @DisplayName("썸네일 업로드 성공-multipartfile")
    void uploadThumbnailTest_success() {
        //given
        File thumbnailFile = mock(File.class);
        MultipartFile file = new MockMultipartFile("images", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes());
        given(thumbnailExtractClient.extractThumbnail(file))
                .willReturn(thumbnailFile);
        given(fileStorageManageService.uploadImage(thumbnailFile, Domain.DIARY))
                .willReturn(Optional.of("DIARY/2024020211223/randomfilename.png"));
        //when
        String result = thumbnailService.uploadThumbnail(file, Domain.DIARY);
        //then
        verify(thumbnailFile, times(1)).delete();
        assertEquals("DIARY/2024020211223/randomfilename.png", result);
    }

    @Test
    @DisplayName("썸네일 업로드 성공-file storage manage service 가 empty 를 리턴-multipartfile")
    void uploadThumbnailTest_success_ReturnDefaultPath() {
        //given
        File thumbnailFile = mock(File.class);
        MultipartFile file = new MockMultipartFile("images", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes());
        given(thumbnailExtractClient.extractThumbnail(file))
                .willReturn(thumbnailFile);
        given(fileStorageManageService.uploadImage(thumbnailFile, Domain.DIARY))
                .willReturn(Optional.empty());
        //when
        String result = thumbnailService.uploadThumbnail(file, Domain.DIARY);
        //then
        verify(thumbnailFile, times(1)).delete();
        assertEquals("RESOURCE/default-thumbnail.png", result);
    }

    @Test
    @DisplayName("썸네일 업로드 성공-file path")
    void uploadThumbnailTest_success_WhenFilePathIsGiven() {
        //given
        File thumbnailFile = mock(File.class);
        String filePath = "temp/video/fasfdas.mp4";
        given(thumbnailExtractClient.extractThumbnail(filePath))
                .willReturn(thumbnailFile);
        given(fileStorageManageService.uploadImage(thumbnailFile, Domain.DIARY))
                .willReturn(Optional.of("DIARY/2024020211223/randomfilename.png"));
        //when
        String result = thumbnailService.uploadThumbnail(filePath, Domain.DIARY);
        //then
        verify(thumbnailFile, times(1)).delete();
        assertEquals("DIARY/2024020211223/randomfilename.png", result);
    }

    @Test
    @DisplayName("썸네일 업로드 성공-file storage manage service 가 empty 를 리턴-file path")
    void uploadThumbnailTest_success_ReturnDefaultPath_WhenFilePathIsGiven() {
        //given
        File thumbnailFile = mock(File.class);
        String filePath = "temp/video/fasfdas.mp4";
        given(thumbnailExtractClient.extractThumbnail(filePath))
                .willReturn(thumbnailFile);
        given(fileStorageManageService.uploadImage(thumbnailFile, Domain.DIARY))
                .willReturn(Optional.empty());
        //when
        String result = thumbnailService.uploadThumbnail(filePath, Domain.DIARY);
        //then
        verify(thumbnailFile, times(1)).delete();
        assertEquals("RESOURCE/default-thumbnail.png", result);
    }
}