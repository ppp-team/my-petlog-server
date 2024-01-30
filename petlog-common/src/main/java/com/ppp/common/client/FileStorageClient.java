package com.ppp.common.client;

import com.ppp.domain.common.constant.FileDomain;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageClient {
    String upload(MultipartFile multipartFile, FileDomain domain);

    void delete(String filePath);
}
