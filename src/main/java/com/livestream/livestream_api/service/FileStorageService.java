package com.livestream.livestream_api.service;


import com.livestream.livestream_api.exception.BadRequestException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES =
            List.of("image/jpeg", "image/png", "image/gif", "image/webp");

    public String storeFile(MultipartFile file) {
        validateFile(file);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            Path target = Paths.get(uploadDir).resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/" + uploadDir + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    public void deleteFile(String filePath) {
        try {
            if (filePath != null) {
                Path path = Paths.get(filePath.startsWith("/") ? filePath.substring(1) : filePath);
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {

        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BadRequestException("File cannot be empty");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new BadRequestException("Only image files (JPEG, PNG, GIF, WEBP) are allowed");
        if (file.getSize() > 10 * 1024 * 1024)
            throw new BadRequestException("File size cannot exceed 10MB");
    }
}