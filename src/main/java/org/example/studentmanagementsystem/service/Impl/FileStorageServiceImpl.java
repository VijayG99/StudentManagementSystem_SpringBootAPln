package org.example.studentmanagementsystem.service.Impl;

import org.example.studentmanagementsystem.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path uploadDirectory =
            Paths.get("uploads").toAbsolutePath().normalize();

    @Override
    public String saveFile(MultipartFile file, String folder) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        try {
            Path folderPath = uploadDirectory.resolve(folder);

            Files.createDirectories(folderPath);

            String originalFileName = file.getOriginalFilename();

            String extension = "";

            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(
                        originalFileName.lastIndexOf(".")
                );
            }

            String fileName = UUID.randomUUID() + extension;

            Path targetPath = folderPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return folder + "/" + fileName;

        } catch (IOException exception) {
            throw new RuntimeException("Failed to store file", exception);
        }
    }

    @Override
    public void deleteFile(String filePath) {

        if (filePath == null || filePath.isBlank()) {
            return;
        }

        try {
            Path path = uploadDirectory.resolve(filePath);
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to delete file", exception);
        }
    }

    @Override
    public byte[] getFile(String filePath) {

        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }

        try {
            Path path = uploadDirectory.resolve(filePath).normalize();

            if (!Files.exists(path)) {
                throw new RuntimeException("File not found");
            }

            return Files.readAllBytes(path);

        } catch (IOException exception) {
            throw new RuntimeException("Failed to read file", exception);
        }
    }


}