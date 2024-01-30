package com.ppp.common.service;

import com.ppp.common.client.FileStorageClient;
import com.ppp.domain.common.constant.FileDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileManageServiceTest {
    @Mock
    private FileStorageClient fileStorageClient;

    @InjectMocks
    private FileManageService fileManageService;

    @Test
    @DisplayName("이미지 업로드-성공")
    void uploadImage_success() {
        //given
        MultipartFile file = new MockMultipartFile("images", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes());
        String savedPath = "USER/2024-01-30/abcdefgthdsfalfakldfsaaf202302041111234.jpg";
        given(fileStorageClient.upload(any(), any()))
                .willReturn(savedPath);
        //when
        Optional<String> maybeString = fileManageService.uploadImage(file, FileDomain.USER);
        //then
        verify(fileStorageClient, times(1)).upload(any(), any());
        assertTrue(maybeString.isPresent());
        assertEquals(savedPath, maybeString.get());
    }

    @Test
    @DisplayName("이미지 업로드-성공-허용하지 않는 확장자가 주어짐")
    void uploadImage_success_GivenNotSupportedExtension() {
        //given
        MultipartFile image = new MockMultipartFile("images", "image.webp",
                MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes());
        //when
        Optional<String> maybeString = fileManageService.uploadImage(image, FileDomain.USER);
        //then
        verify(fileStorageClient, times(0)).upload(any(), any());
        assertTrue(maybeString.isEmpty());
    }

    @Test
    @DisplayName("이미지 리스트 업로드 성공")
    void uploadImages_success() {
        //given
        List<MultipartFile> images = List.of(
                new MockMultipartFile("images", "image.jpg",
                        MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()),
                new MockMultipartFile("images", "image.webp",
                        MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes()));
        String savedPath = "USER/2024-01-30/abcdefgthdsfalfakldfsaaf202302041111234.jpg";
        given(fileStorageClient.upload(any(), any()))
                .willReturn(savedPath);
        //when
        List<String> filePaths = fileManageService.uploadImages(images, FileDomain.USER);
        //then
        verify(fileStorageClient, times(1)).upload(any(), any());
        assertEquals(filePaths.size(), 1);
        assertTrue(filePaths.contains(savedPath));
    }
}