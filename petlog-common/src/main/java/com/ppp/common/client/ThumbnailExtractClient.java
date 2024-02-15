package com.ppp.common.client;

import com.ppp.common.exception.FileException;
import com.ppp.domain.common.constant.FileType;

import java.io.File;

public interface ThumbnailExtractClient {
    File extractThumbnail(File input, FileType type) throws FileException;
}
