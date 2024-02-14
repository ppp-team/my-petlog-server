package com.ppp.common.service;

import com.ppp.common.client.FileStorageClient;
import com.ppp.domain.common.constant.Domain;
import com.ppp.domain.video.TempVideo;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileStorageManageServiceTest {
    @Mock
    private FileStorageClient fileStorageClient;

    @InjectMocks
    private FileStorageManageService fileStorageManageService;

    @Test
    @DisplayName("이미지 업로드-성공")
    void uploadImage_success() {
        //given
        MultipartFile file = new MockMultipartFile("images", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, "abcde" .getBytes());
        String savedPath = "USER/2024-01-30/abcdefgthdsfalfakldfsaaf202302041111234.jpg";
        given(fileStorageClient.upload((MultipartFile) any(), any()))
                .willReturn(savedPath);
        //when
        Optional<String> maybeString = fileStorageManageService.uploadImage(file, Domain.USER);
        //then
        verify(fileStorageClient, times(1)).upload((MultipartFile) any(), any());
        assertTrue(maybeString.isPresent());
        assertEquals(savedPath, maybeString.get());
    }

    @Test
    @DisplayName("이미지 업로드-성공-허용하지 않는 확장자가 주어짐")
    void uploadImage_success_GivenNotSupportedExtension() {
        //given
        MultipartFile image = new MockMultipartFile("images", "image.webp",
                MediaType.IMAGE_JPEG_VALUE, "abcde" .getBytes());
        //when
        Optional<String> maybeString = fileStorageManageService.uploadImage(image, Domain.USER);
        //then
        verify(fileStorageClient, times(0)).upload((MultipartFile) any(), any());
        assertTrue(maybeString.isEmpty());
    }

    @Test
    @DisplayName("이미지 리스트 업로드 성공")
    void uploadImages_success() {
        //given
        List<MultipartFile> images = List.of(
                new MockMultipartFile("images", "image.jpg",
                        MediaType.IMAGE_JPEG_VALUE, "abcde" .getBytes()),
                new MockMultipartFile("images", "image.webp",
                        MediaType.IMAGE_JPEG_VALUE, "abcde" .getBytes()));
        String savedPath = "USER/2024-01-30/abcdefgthdsfalfakldfsaaf202302041111234.jpg";
        given(fileStorageClient.upload((MultipartFile) any(), any()))
                .willReturn(savedPath);
        //when
        List<String> filePaths = fileStorageManageService.uploadImages(images, Domain.USER);
        //then
        verify(fileStorageClient, times(1)).upload((MultipartFile) any(), any());
        assertEquals(filePaths.size(), 1);
        assertTrue(filePaths.contains(savedPath));
    }

    @Test
    @DisplayName("비디오 업로드-성공")
    void uploadVideo_success() {
        //given
        TempVideo tempVideo = TempVideo.builder()
                .id("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .filePath("temp/encoded/2024021313/267d730ad30d4c8da5560e9b3cc0581820240213130549683.mp4")
                .build();
        String savedPath = "USER/2024-01-30/abcdefgthdsfalfakldfsaaf202302041111234.jpg";
        given(fileStorageClient.upload((File) any(), any()))
                .willReturn(savedPath);
        //when
        Optional<String> maybeString = fileStorageManageService.uploadVideo(tempVideo, Domain.USER);
        //then
        verify(fileStorageClient, times(1)).upload((File) any(), any());
        assertTrue(maybeString.isPresent());
        assertEquals(savedPath, maybeString.get());
    }

    @Test
    @DisplayName("비디오 업로드-성공-허용하지 않는 확장자가 주어짐")
    void uploadVideo_success_GivenNotSupportedExtension() {
        //given
        TempVideo tempVideo = TempVideo.builder()
                .id("c8e8f796-8e29-4067-86c4-0eae419a054e")
                .filePath("temp/encoded/2024021313/267d730ad30d4c8da5560e9b3cc0581820240213130549683.avchd")
                .build();
        //when
        Optional<String> maybeString = fileStorageManageService.uploadVideo(tempVideo, Domain.USER);
        //then
        verify(fileStorageClient, times(0)).upload((File) any(), any());
        assertTrue(maybeString.isEmpty());
    }

    @Test
    @DisplayName("비디오 리스트 업로드 성공")
    void uploadVideos_success() {
        //given
        List<TempVideo> images = List.of(TempVideo.builder()
                        .id("c8e8f796-8e29-4067-86c4-0eae419a054e")
                        .filePath("temp/encoded/2024021313/267d730ad30d4c8da5560e9b3cc0581820240213130549683.mp4")
                        .build(),
                TempVideo.builder()
                        .id("c8e8f796-8e29-4067-86c4-0eae419a054e")
                        .filePath("temp/encoded/2024021313/267d730ad30d4c8da5560e9b3cc0581820240213130549683.avhcd")
                        .build());
        String savedPath = "USER/2024-01-30/abcdefgthdsfalfakldfsaaf202302041111234.jpg";
        given(fileStorageClient.upload((File) any(), any()))
                .willReturn(savedPath);
        //when
        List<String> filePaths = fileStorageManageService.uploadVideos(images, Domain.USER);
        //then
        verify(fileStorageClient, times(1)).upload((File) any(), any());
        assertEquals(filePaths.size(), 1);
        assertTrue(filePaths.contains(savedPath));
    }

}