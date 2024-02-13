package com.ppp.api.video.service;

import com.ppp.api.video.dto.response.VideoResponse;
import com.ppp.api.video.exception.VideoException;
import com.ppp.common.client.VideoConvertClient;
import com.ppp.domain.user.User;
import com.ppp.domain.video.TempVideo;
import com.ppp.domain.video.repository.TempVideoRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static com.ppp.api.video.exception.ErrorCode.NOT_ALLOWED_EXTENSION;
import static com.ppp.api.video.exception.ErrorCode.VIDEO_UPLOAD_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VideoManageServiceTest {
    @Mock
    private VideoConvertClient videoConvertClient;
    @Mock
    private TempVideoRedisRepository tempVideoRedisRepository;
    @InjectMocks
    private VideoManageService videoManageService;
    User user = User.builder()
            .id("abcde1234")
            .nickname("hi")
            .build();

    @Test
    @DisplayName("비디오 업로드 성공")
    void uploadTempVideo_success() {
        //given
        MultipartFile video = new MockMultipartFile("video", "video.wmv", MediaType.IMAGE_JPEG_VALUE, "abcde" .getBytes());
        String domain = "DIARY";
        String convertedVideoPath = "temp/encoded/2024021313/267d730ad30d4c8da5560e9b3cc0581820240213130549683.mp4";
        given(videoConvertClient.compress(any(), any()))
                .willReturn(convertedVideoPath);
        given(tempVideoRedisRepository.save(any()))
                .willReturn(TempVideo.builder()
                        .id("random-string")
                        .build());
        LocalDateTime now = LocalDateTime.now();
        //when
        VideoResponse response = videoManageService.uploadTempVideo(user, domain, video);
        ArgumentCaptor<TempVideo> captor = ArgumentCaptor.forClass(TempVideo.class);
        //then
        verify(tempVideoRedisRepository, times(1)).save(captor.capture());
        assertEquals(captor.getValue().getFilePath(), convertedVideoPath);
        assertEquals(captor.getValue().getUserId(), user.getId());
        assertEquals(response.videoId(), "random-string");
        assertTrue(response.validUntil().isAfter(now.plusMinutes(170)) && response.validUntil().isBefore(now.plusMinutes(180)));
    }

    @Test
    @DisplayName("비디오 업로드 실패-video upload not allowed-허용되지 않는 도메인")
    void uploadTempVideo_fail_VIDEO_UPLOAD_NOT_ALLOWED_WhenDomainHasNotAllowable() {
        //given
        MultipartFile video = new MockMultipartFile("video", "video.wmv", MediaType.IMAGE_JPEG_VALUE, "abcde" .getBytes());
        String domain = "USER";
        //when
        VideoException exception = assertThrows(VideoException.class, () -> videoManageService.uploadTempVideo(user, domain, video));
        //then
        assertEquals(VIDEO_UPLOAD_NOT_ALLOWED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("비디오 업로드 실패-video upload not allowed-비디오가 비어있음")
    void uploadTempVideo_fail_VIDEO_UPLOAD_NOT_ALLOWED_WhenVideoIsEmpty() {
        //given
        MultipartFile video = new MockMultipartFile("video", "video.wmv", MediaType.IMAGE_JPEG_VALUE, "".getBytes());
        String domain = "DIARY";
        //when
        VideoException exception = assertThrows(VideoException.class, () -> videoManageService.uploadTempVideo(user, domain, video));
        //then
        assertEquals(VIDEO_UPLOAD_NOT_ALLOWED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("비디오 업로드 실패-now allowed extension")
    void uploadTempVideo_fail_NOT_ALLOWED_EXTENSION() {
        //given
        MultipartFile video = new MockMultipartFile("video", "video.abcde", MediaType.IMAGE_JPEG_VALUE, "abcde".getBytes());
        String domain = "DIARY";
        //when
        VideoException exception = assertThrows(VideoException.class, () -> videoManageService.uploadTempVideo(user, domain, video));
        //then
        assertEquals(NOT_ALLOWED_EXTENSION.getCode(), exception.getCode());
    }
}