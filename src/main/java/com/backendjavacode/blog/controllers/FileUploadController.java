package com.backendjavacode.blog.controllers;

import com.backendjavacode.blog.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file/uploading")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;
    @PostMapping("/uploadMultiple")
    public ResponseEntity<String> uploadMultipleFiles(@RequestParam("files") List<MultipartFile> files) {
        try {
            fileUploadService.saveMultipleFiles(files);
            return ResponseEntity.status(HttpStatus.OK).body("Files uploaded successfully....!!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload files: " + e.getMessage());
        }
    }
}
