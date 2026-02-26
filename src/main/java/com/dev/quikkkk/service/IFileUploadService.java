package com.dev.quikkkk.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IFileUploadService {
    String storeVideoFile(MultipartFile file);

    String storeImageFile(MultipartFile file);

    ResponseEntity<Resource> serveVideo(String id, String rangeHeader);

    ResponseEntity<Resource> serveImage(String id, String rangeHeader);
}
