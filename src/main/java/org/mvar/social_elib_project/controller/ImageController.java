package org.mvar.social_elib_project.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.mvar.social_elib_project.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        try {
            String id = imageService.saveImage(file);
            return ResponseEntity.ok(id);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getImage(@PathVariable String id) {
        GridFSFile file = imageService.getImageById(id);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            InputStreamResource resource = new InputStreamResource(imageService.getImageStream(file));
            String contentType = imageService.getContentType(file);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : APPLICATION_OCTET_STREAM.toString()))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading image: " + e.getMessage());
        }
    }
}



