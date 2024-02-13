package com.ppp.common.client;

import com.ppp.common.exception.FileException;
import com.ppp.common.util.FilePathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.ppp.common.exception.ErrorCode.FILE_UPLOAD_FAILED;

@Component
@Slf4j
@RequiredArgsConstructor
public class FfmpegClient implements VideoConvertClient {
    private final FFmpegExecutor fFmpegExecutor;

    @Override
    public String compress(MultipartFile file) {
        try {
            File dirToBeSaved = new File(DEFAULT_PATH + "/" + LocalDateTime.now().format(dateTimeFormatter));
            if (!dirToBeSaved.exists()) dirToBeSaved.mkdirs();
            Path inputPath = Paths.get(dirToBeSaved.getPath(), file.getOriginalFilename());
            Files.write(inputPath, file.getBytes());
            Path outputPath = Paths.get(dirToBeSaved.getPath(), FilePathUtil.createFileName() + ".mp4");
            Files.createFile(outputPath);

            fFmpegExecutor.createJob(
                    new FFmpegBuilder()
                            .overrideOutputFiles(true)
                            .setInput(inputPath.toString())
                            .addOutput(outputPath.toString())
                            .addExtraArgs("-vf", "scale=-1:480")
                            .done(), progress -> log.info(getClass() + " : resolution proceeding " + progress)).run();
            Files.delete(inputPath);

            if (Files.size(outputPath) == 0)
                throw new FileException(FILE_UPLOAD_FAILED);

            return outputPath.toString();
        } catch (IOException e) {
            throw new FileException(FILE_UPLOAD_FAILED);
        }
    }
}
