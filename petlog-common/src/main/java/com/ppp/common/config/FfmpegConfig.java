package com.ppp.common.config;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FfmpegConfig {
    @Value("${ffmpeg.path}")
    private String path;

    @Bean
    public FFmpegExecutor ffmpegExecutor() throws IOException {
        return new FFmpegExecutor(fFmpeg(), fFprobe());
    }

    @Bean
    public FFmpeg fFmpeg() throws IOException {
        return new FFmpeg(path + "/ffmpeg");
    }

    @Bean
    public FFprobe fFprobe() throws IOException {
        return new FFprobe(path + "/ffprobe");
    }
}
