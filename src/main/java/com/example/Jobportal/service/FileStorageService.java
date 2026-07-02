package com.example.Jobportal.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeResume(MultipartFile file, Long candidateId);
    Resource loadResumeAsResource(String storedFilename);
}