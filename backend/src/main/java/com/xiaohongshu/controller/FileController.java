package com.xiaohongshu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class FileController {

    private final Path fileStorageLocation;

    public FileController(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("FileController is working!");
    }

    @GetMapping("/debug/{fileName:.+}")
    public ResponseEntity<String> debugPath(@PathVariable String fileName) {
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        return ResponseEntity.ok("Resolved path: " + filePath.toAbsolutePath().toString() + ", Exists: "
                + java.nio.file.Files.exists(filePath));
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                String contentType = "application/octet-stream";
                String lowerFileName = fileName.toLowerCase();

                if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (lowerFileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (lowerFileName.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (lowerFileName.endsWith(".mp4")) {
                    contentType = "video/mp4";
                } else if (lowerFileName.endsWith(".svg")) {
                    contentType = "image/svg+xml";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body(null);
                // For debugging:
                // return ResponseEntity.status(404).body(new UrlResource(Paths.get("File not
                // found: " + filePath.toString()).toUri()));
                // typically we can't return Resource for string message easily without changing
                // return type.
                // Let's create a temporary specific endpoint for debug or just trust my
                // analysis.
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @org.springframework.web.bind.annotation.PostMapping("")
    public ResponseEntity<?> uploadFile(
            @org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            java.nio.file.Files.copy(file.getInputStream(), targetLocation,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(java.util.Map.of("url", fileUrl));
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        }
    }
}
