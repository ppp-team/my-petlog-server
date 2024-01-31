package com.ppp.common.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ppp.common.exception.FileException;
import com.ppp.common.util.FilePathUtil;
import com.ppp.domain.common.constant.Domain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.ppp.common.exception.ErrorCode.FILE_UPLOAD_FAILED;

@Component
@Slf4j
@RequiredArgsConstructor
public class S3Client implements FileStorageClient {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    @Override
    public String upload(MultipartFile multipartFile, Domain domain) {
        String fileName = FilePathUtil.createFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String filePath = FilePathUtil.createFilePath(domain);
        try {
            File file = File.createTempFile("file", fileName);
            multipartFile.transferTo(file);
            amazonS3.putObject(new PutObjectRequest(bucket, filePath + fileName, file));
        } catch (IOException e) {
            throw new FileException(FILE_UPLOAD_FAILED);
        }
        return filePath + fileName;
    }

    @Override
    public void delete(String filePath) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }
}
