package com.dev.quikkkk.utils;

import com.dev.quikkkk.enums.ErrorCode;
import com.dev.quikkkk.exception.BusinessException;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public class FileHandlerUtils {
    private static final Map<String, String> VIDEO_MIME_TYPES = Map.ofEntries(
            Map.entry("webm", "video/webm"),
            Map.entry("ogg", "video/ogg"),
            Map.entry("mkv", "video/x-matroska"),
            Map.entry("avi", "video/x-msvideo"),
            Map.entry("mov", "video/quicktime"),
            Map.entry("flv", "video/x-flv"),
            Map.entry("wmv", "video/x-ms-wmv"),
            Map.entry("m4v", "video/x-m4v"),
            Map.entry("3gp", "video/3gpp"),
            Map.entry("mpg", "video/mpeg"),
            Map.entry("mpeg", "video/mpeg")
    );

    private static final Map<String, String> IMAGE_MIME_TYPES = Map.ofEntries(
            Map.entry("png", "image/png"),
            Map.entry("gif", "image/gif"),
            Map.entry("webp", "image/webp")
    );

    public static String extractFileExtension(String originalFilename) {
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains("."))
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

        return fileExtension;
    }

    public static Path findFileById(Path directory, String id) throws IOException {
        try (var paths = Files.list(directory)) {
            return paths
                    .filter(path -> path.getFileName().toString().startsWith(id))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
        }
    }

    public static String detectVideoContentType(String filename) {
        if (filename == null || !filename.contains(".")) return "video/mp4";
        String extension = filename
                .substring(filename.lastIndexOf(".") + 1)
                .toLowerCase();

        return VIDEO_MIME_TYPES.getOrDefault(extension, "video/mp4");
    }

    public static String detectImageContentType(String filename) {
        if (filename == null || !filename.contains(".")) return "image/jpeg";
        String extension = filename
                .substring(filename.lastIndexOf(".") + 1)
                .toLowerCase();

        return IMAGE_MIME_TYPES.getOrDefault(extension, "image/jpeg");
    }

    public static long[] parseRangeHeader(String rangeHeader, long fileLength) {
        String[] ranges = rangeHeader.replace("bytes=", "").split("_");

        long rangeStart = Long.parseLong(ranges[0]);
        long rangeEnd = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileLength;

        return new long[]{rangeStart, rangeEnd};
    }

    public static Resource createRangeResource(
            Path filePath,
            long rangeStart,
            long rangeLength
    ) throws IOException {
        FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ);
        channel.position(rangeStart);

        InputStream stream = Channels.newInputStream(channel);
        InputStream limitedStream = new LimitedInputStream(stream, rangeLength) {
            @Override
            protected void raiseError(long pSizeMax, long pCount) throws IOException {
                throw new BusinessException(ErrorCode.INVALID_FILE_RANGE);
            }
        };

        return new InputStreamResource(limitedStream) {
            @Override
            public long contentLength() {
                return rangeLength;
            }
        };
    }

    public static Resource createFullResource(Path filePath) throws MalformedURLException {
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        return resource;
    }
}
