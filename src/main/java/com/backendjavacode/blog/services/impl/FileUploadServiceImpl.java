package com.backendjavacode.blog.services.impl;

import com.backendjavacode.blog.services.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final String uploadDir = "D:\\Uploading Files from backend";

    @Override
    public void saveMultipleFiles(List<MultipartFile> files) {
        try {
            // Create the directory if it doesn't exist
            Path directoryPath = Paths.get(uploadDir);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            for (MultipartFile file : files) {
                // Get the file's original name and construct the full path
                String originalFileName = file.getOriginalFilename();
                Path filePath = directoryPath.resolve(originalFileName);

                // Save the file to the specified path
                Files.write(filePath, file.getBytes());
            }

        } catch (IOException e) {
            // Handle the exception according to your needs
            throw new RuntimeException("Failed to store files", e);
        }
    }
}
