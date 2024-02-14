package com.ppp.common.client;

import com.ppp.domain.common.constant.Domain;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileStorageClient {
    String upload(MultipartFile multipartFile, Domain domain);

    String upload(File file, Domain domain);

    void delete(String filePath);
}
