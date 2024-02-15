package com.ppp.common.client;

import com.ppp.common.exception.FileException;
import com.ppp.common.util.FilePathUtil;
import com.ppp.domain.common.constant.FileType;
import com.ppp.domain.common.constant.VideoCompressType;
import lombok.RequiredArgsConstructor;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.ppp.common.exception.ErrorCode.EXTRACT_THUMBNAIL_FAILED;
import static com.ppp.common.exception.ErrorCode.FILE_UPLOAD_FAILED;

@Component
@RequiredArgsConstructor
public class FfmpegClient implements VideoConvertClient, ThumbnailExtractClient {
    private final FFmpegExecutor fFmpegExecutor;
    private final FFmpeg fFmpeg;
    private static final String VIDEO_EXTENSION = ".mp4";
    private static final String THUMBNAIL_EXTENSION = ".png";

    @Override
    public String compress(MultipartFile file, VideoCompressType compressType) {
        try {
            File dirToBeSaved = new File(DEFAULT_PATH + "/" + LocalDateTime.now().format(dateTimeFormatter));
            if (!dirToBeSaved.exists()) dirToBeSaved.mkdirs();
            Path inputPath = Paths.get(dirToBeSaved.getPath(), file.getOriginalFilename());
            Files.write(inputPath, file.getBytes());
            Path outputPath = Paths.get(dirToBeSaved.getPath(), FilePathUtil.createFileName() + VIDEO_EXTENSION);
            Files.createFile(outputPath);

            fFmpegExecutor.createJob(
                    new FFmpegBuilder()
                            .overrideOutputFiles(true)
                            .setInput(inputPath.toString())
                            .addOutput(outputPath.toString())
                            .addExtraArgs("-vf", "scale=-1:" + compressType.getResolution())
                            .done(), progress -> log.info(getClass() + " : resolution proceeding " + progress)).run();
            Files.delete(inputPath);

            if (Files.size(outputPath) == 0)
                throw new FileException(FILE_UPLOAD_FAILED);

            return outputPath.toString();
        } catch (Exception e) {
            throw new FileException(FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public File extractThumbnail(File input, FileType type) throws FileException {
        if (FileType.IMAGE.equals(type))
            return extractThumbnailFromImage(input);
        return extractThumbnailFromVideo(input);
    }

    public File extractThumbnailFromImage(File input) throws FileException {
        try {
            File output = File.createTempFile("s_temp", THUMBNAIL_EXTENSION);
            fFmpeg.run(new FFmpegBuilder()
                    .overrideOutputFiles(true)
                    .setInput(input.getPath())
                    .addOutput(output.getPath())
                    .addExtraArgs("-vf", "scale=160:160")
                    .done());
            FileUtils.delete(input);
            return output;
        } catch (Exception e) {
            log.error("Class : {}, Code : {}, Message : {}", getClass(), EXTRACT_THUMBNAIL_FAILED.getCode(), EXTRACT_THUMBNAIL_FAILED.getMessage());
            throw new FileException(EXTRACT_THUMBNAIL_FAILED);
        }
    }

    public File extractThumbnailFromVideo(File input) throws FileException {
        try {
            File output = File.createTempFile("s_temp", THUMBNAIL_EXTENSION);
            fFmpeg.run(new FFmpegBuilder()
                    .setInput(input.getPath())
                    .overrideOutputFiles(true)
                    .addOutput(output.getPath())
                    .addExtraArgs("-ss", "00:00:01")
                    .addExtraArgs("-vf", "scale=160:160")
                    .setFrames(1)
                    .done());
            FileUtils.delete(input);
            return output;
        } catch (Exception e) {
            log.error("Class : {}, Code : {}, Message : {}", getClass(), EXTRACT_THUMBNAIL_FAILED.getCode(), EXTRACT_THUMBNAIL_FAILED.getMessage());
            throw new FileException(EXTRACT_THUMBNAIL_FAILED);
        }
    }

}
