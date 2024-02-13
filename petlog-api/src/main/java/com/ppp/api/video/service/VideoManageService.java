package com.ppp.api.video.service;

import com.ppp.api.video.dto.response.VideoResponse;
import com.ppp.api.video.exception.ErrorCode;
import com.ppp.api.video.exception.VideoException;
import com.ppp.common.client.VideoConvertClient;
import com.ppp.common.util.FilePathUtil;
import com.ppp.domain.common.constant.Domain;
import com.ppp.domain.common.constant.VideoCompressType;
import com.ppp.domain.user.User;
import com.ppp.domain.video.TempVideo;
import com.ppp.domain.video.repository.TempVideoRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VideoManageService {
    public static final List<String> ALLOW_VIDEO_CODES = List.of(".mp4", ".mov", ".wmv", ".avi", ".avchd", ".webm", ".flv");
    private final VideoConvertClient videoConvertClient;
    private final TempVideoRedisRepository tempVideoRedisRepository;

    public VideoResponse uploadTempVideo(User user, String domain, MultipartFile video) {
        if (!Domain.valueOf(domain).isHasVideo() || video.isEmpty())
            throw new VideoException(ErrorCode.VIDEO_UPLOAD_NOT_ALLOWED);
        FilePathUtil.getFileExtension(Objects.requireNonNull(video.getOriginalFilename()))
                .filter(extension -> ALLOW_VIDEO_CODES.contains(extension.toLowerCase(Locale.ROOT)))
                .orElseThrow(() -> new VideoException(ErrorCode.NOT_ALLOWED_EXTENSION));

        String convertedVideoPath = videoConvertClient.compress(video, VideoCompressType.LOW);
        return VideoResponse.from(tempVideoRedisRepository.save(TempVideo.builder()
                .filePath(convertedVideoPath)
                .userId(user.getId())
                .build()));
    }

    @Scheduled(cron = "0 0 0/3 * * *")
    public void deleteTempVideo() {
        videoConvertClient.deleteTempVideoCreatedBefore(3);
    }
}

