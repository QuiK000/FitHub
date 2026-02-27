package com.dev.quikkkk.service.impl;

import com.dev.quikkkk.enums.ErrorCode;
import com.dev.quikkkk.exception.BusinessException;
import com.dev.quikkkk.service.IFileUploadService;
import com.dev.quikkkk.utils.FileHandlerUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.ACCEPT_RANGES;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_RANGE;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements IFileUploadService {
    private Path videoStorageLocation;
    private Path imageStorageLocation;

    @Value("${file.upload.video-dir:uploads/videos}")
    private String videoDir;

    @Value("${file.upload.image-dir:uploads/images}")
    private String imageDir;

    @PostConstruct
    public void init() {
        this.videoStorageLocation = Paths.get(videoDir).toAbsolutePath().normalize();
        this.imageStorageLocation = Paths.get(imageDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(videoStorageLocation);
            Files.createDirectories(imageStorageLocation);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_STORAGE_DIRECTORY_CREATION_FAILED);
        }
    }

    @Override
    public String storeVideoFile(MultipartFile file) {
        return storeFile(file, videoStorageLocation);
    }

    @Override
    public String storeImageFile(MultipartFile file) {
        return storeFile(file, imageStorageLocation);
    }

    @Override
    public ResponseEntity<Resource> serveVideo(String id, String rangeHeader) {
        try {
            Path filePath = FileHandlerUtils.findFileById(videoStorageLocation, id);
            Resource resource = FileHandlerUtils.createFullResource(filePath);

            String filename = resource.getFilename();
            String contentType = FileHandlerUtils.detectVideoContentType(filename);
            long fileLength = resource.contentLength();

            if (isFullContentRequest(rangeHeader)) {
                return buildFullVideoResponse(
                        resource,
                        contentType,
                        filename,
                        fileLength
                );
            }

            return buildPartialVideoResponse(filePath, rangeHeader, contentType, filename, fileLength);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Resource> serveImage(String id, String rangeHeader) {
        try {
            Path filePath = FileHandlerUtils.findFileById(imageStorageLocation, id);
            Resource resource = FileHandlerUtils.createFullResource(filePath);

            String filename = resource.getFilename();
            String contentType = FileHandlerUtils.detectImageContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String storeFile(MultipartFile file, Path storageLocation) {
        String fileExtension = FileHandlerUtils.extractFileExtension(file.getOriginalFilename());
        String id = UUID.randomUUID().toString();
        String filename = id + fileExtension;

        try {
            if (file.isEmpty()) throw new BusinessException(ErrorCode.FILE_EMPTY);

            Path targetLocation = storageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return id;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_STORAGE_FAILED);
        }
    }

    private boolean isFullContentRequest(String rangeHeader) {
        return rangeHeader == null || rangeHeader.isEmpty();
    }

    private ResponseEntity<Resource> buildFullVideoResponse(
            Resource resource,
            String contentType,
            String filename,
            long fileLength
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .header(ACCEPT_RANGES, "bytes")
                .header(CONTENT_LENGTH, String.valueOf(fileLength))
                .body(resource);
    }

    private ResponseEntity<Resource> buildPartialVideoResponse(
            Path filePath,
            String rangeHeader,
            String contentType,
            String filename,
            long fileLength
    ) throws IOException {
        long[] range = FileHandlerUtils.parseRangeHeader(rangeHeader, fileLength);
        long rangeStart = range[0];
        long rangeEnd = range[1];

        if (!isValidRange(rangeStart, rangeEnd, fileLength)) return buildRangeNoSatisfiableResponse(fileLength);
        long contentLength = rangeEnd - rangeStart + 1;

        Resource rangeResource = FileHandlerUtils.createRangeResource(filePath, rangeStart, contentLength);
        return ResponseEntity.status(206)
                .contentType(MediaType.parseMediaType(contentType))
                .header(CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .header(ACCEPT_RANGES, "bytes")
                .header(CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength)
                .header(CONTENT_LENGTH, String.valueOf(contentLength))
                .body(rangeResource);
    }

    private boolean isValidRange(long rangeStart, long rangeEnd, long fileLength) {
        return rangeStart <= rangeEnd && rangeStart >= 0 && rangeEnd < fileLength;
    }

    private ResponseEntity<Resource> buildRangeNoSatisfiableResponse(long fileLength) {
        return ResponseEntity.status(416)
                .header(CONTENT_RANGE, "bytes */" + fileLength)
                .build();
    }
}
