package com.ppp.common.client;

import com.ppp.domain.common.constant.Domain;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageClient {
    String upload(MultipartFile multipartFile, Domain domain);

    void delete(String filePath);
}
