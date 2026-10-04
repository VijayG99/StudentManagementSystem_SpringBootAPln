package org.example.studentmanagementsystem.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String saveFile(MultipartFile file, String folder);

    void deleteFile(String filePath);

    byte[] getFile(String filePath);
}