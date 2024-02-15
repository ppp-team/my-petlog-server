package com.ppp.common.service;

import com.ppp.common.client.ThumbnailExtractClient;
import com.ppp.common.exception.FileException;
import com.ppp.domain.common.constant.Domain;
import com.ppp.domain.common.constant.FileType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
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

    static MockedConstruction<URL> mockURL;
    static MockedStatic<FileUtils> mockFiles;
    static MockedStatic<File> mockFile;

    @AfterEach
    void tearDown() {
        mockURL.close();
        mockFile.close();
        mockFiles.close();
    }

    @Test
    @DisplayName("썸네일 업로드 성공")
    void uploadThumbnailTest_success() throws IOException {
        //given
        String filePath = "temp/video/fasfdas.mp4";
        mockURL = mockConstruction(URL.class, (mock, context) -> {
            given(mock.openStream()).willReturn(InputStream.nullInputStream());
        });
        mockFiles = mockStatic(FileUtils.class);
        FileUtils.copyInputStreamToFile(any(), any());
        mockFile = mockStatic(File.class);
        File inputFile = Path.of("DIARY/2024020211223/randomfilename.mp4").toFile();
        when(File.createTempFile(anyString(), anyString())).thenReturn(inputFile);
        File thumbnailFile = mock(File.class);
        given(thumbnailExtractClient.extractThumbnail(inputFile, FileType.VIDEO))
                .willReturn(thumbnailFile);
        given(fileStorageManageService.uploadImage(thumbnailFile, Domain.DIARY))
                .willReturn(Optional.of("DIARY/2024020211223/thumbnail.png"));
        //when
        String result = thumbnailService.uploadThumbnailFromStorageFile(filePath, FileType.VIDEO, Domain.DIARY);
        //then
        verify(thumbnailFile, times(1)).delete();
        assertEquals("DIARY/2024020211223/thumbnail.png", result);
    }

    @Test
    @DisplayName("썸네일 업로드 성공_에러 발생시 디폴트 썸네일 반환")
    void uploadThumbnailTest_success_WhenAnyExceptionOccurred() throws IOException {
        //given
        String filePath = "temp/video/fasfdas.mp4";
        mockURL = mockConstruction(URL.class, (mock, context) -> {
            given(mock.openStream()).willReturn(InputStream.nullInputStream());
        });
        mockFiles = mockStatic(FileUtils.class);
        FileUtils.copyInputStreamToFile(any(), any());
        mockFile = mockStatic(File.class);
        File inputFile = Path.of("DIARY/2024020211223/randomfilename.mp4").toFile();
        when(File.createTempFile(anyString(), anyString())).thenReturn(inputFile);
        when(thumbnailExtractClient.extractThumbnail(inputFile, FileType.VIDEO))
                .thenThrow(FileException.class);
        //when
        String result = thumbnailService.uploadThumbnailFromStorageFile(filePath, FileType.VIDEO, Domain.DIARY);
        //then
        assertEquals("RESOURCE/default-thumbnail.png", result);
    }
}