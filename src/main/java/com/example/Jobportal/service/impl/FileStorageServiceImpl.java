package com.example.Jobportal.service.impl;

import com.example.Jobportal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads/resumes}")
    private String uploadDir;
    
    @Value("${file.image-upload-dir:uploads/images}")
    private String imageUploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public String storeResume(MultipartFile file, Long candidateId) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Resume file is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Resume file must be under 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if (!extension.equals(".pdf") && !extension.equals(".doc") && !extension.equals(".docx")) {
            throw new RuntimeException("Only PDF, DOC, or DOCX files are allowed");
        }

        // random name, never trust the original filename for storage
        String storedFilename = "candidate_" + candidateId + "_" + UUID.randomUUID() + extension;

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(storedFilename).normalize();

            // defence in depth: ensure resolved path is still inside uploadDir
            if (!targetLocation.startsWith(uploadPath)) {
                throw new RuntimeException("Invalid file name");
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store resume file: " + e.getMessage());
        }
    }

    @Override
    public Resource loadResumeAsResource(String storedFilename) {
        return loadResource(storedFilename, uploadDir, "Resume");
    }

    @Override
    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Image file must be under 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png") && !extension.equals(".webp")) {
            throw new RuntimeException("Only JPG, JPEG, PNG, or WEBP files are allowed");
        }

        String storedFilename = "img_" + UUID.randomUUID() + extension;

        try {
            Path uploadPath = Paths.get(imageUploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(storedFilename).normalize();

            if (!targetLocation.startsWith(uploadPath)) {
                throw new RuntimeException("Invalid file name");
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image file: " + e.getMessage());
        }
    }

    @Override
    public Resource loadImageAsResource(String storedFilename) {
        return loadResource(storedFilename, imageUploadDir, "Image");
    }

    private Resource loadResource(String storedFilename, String dir, String type) {
        try {
            Path uploadPath = Paths.get(dir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(storedFilename).normalize();

            if (!filePath.startsWith(uploadPath)) {
                throw new RuntimeException("Invalid file path");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException(type + " file not found or not readable");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(type + " file not found: " + e.getMessage());
        }
    }
}