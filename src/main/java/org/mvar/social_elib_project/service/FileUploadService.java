package org.mvar.social_elib_project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
    private final String uploadDir = "uploads/pdf";

    public String savePdf(MultipartFile file) throws IOException {
        if (!file.getOriginalFilename().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed.");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return "/files/pdf/" + filename;
    }
}
