package com.xiaohongshu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml",
            "video/mp4", "video/webm", "video/ogg");
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    private final Path fileStorageLocation;

    public FileController(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
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

    // 文件下载由 WebConfig 的静态资源处理器处理

    @PostMapping("")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "文件为空"));
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "文件大小不能超过50MB"));
            }

            String contentType = file.getContentType();
            String originalName = file.getOriginalFilename();
            if (!isAllowed(contentType, originalName)) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "不支持的文件类型"));
            }

            // 仅使用 UUID + 安全扩展名
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
                if (!ext.toLowerCase().matches("\\.(jpg|jpeg|png|gif|webp|svg|mp4|webm|ogg)")) {
                    return ResponseEntity.badRequest().body(java.util.Map.of("message", "不支持的文件格式"));
                }
            }
            String fileName = java.util.UUID.randomUUID().toString() + ext;
            Path targetLocation = fileStorageLocation.resolve(fileName);

            if (!targetLocation.normalize().startsWith(fileStorageLocation)) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "Invalid file path"));
            }

            Files.copy(file.getInputStream(), targetLocation,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(java.util.Map.of("url", fileUrl));
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(java.util.Map.of("message", "文件上传失败"));
        }
    }

    private String detectContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".webm")) return "video/webm";
        if (lower.endsWith(".ogg")) return "video/ogg";
        return "application/octet-stream";
    }

    private boolean isAllowed(String contentType, String fileName) {
        if (contentType != null && ALLOWED_TYPES.contains(contentType)) {
            return true;
        }
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                || lower.endsWith(".gif") || lower.endsWith(".webp") || lower.endsWith(".svg")
                || lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".ogg");
    }
}
