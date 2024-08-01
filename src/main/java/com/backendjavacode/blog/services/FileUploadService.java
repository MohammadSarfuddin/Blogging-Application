package com.backendjavacode.blog.services;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileUploadService {

    public void saveMultipleFiles(List<MultipartFile> files);
}
