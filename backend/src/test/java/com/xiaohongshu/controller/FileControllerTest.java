package com.xiaohongshu.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final Path UPLOAD_DIR = Paths.get("./test-uploads").toAbsolutePath().normalize();

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(UPLOAD_DIR);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up test files
        if (Files.exists(UPLOAD_DIR)) {
            Files.list(UPLOAD_DIR).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (Exception ignored) {
                }
            });
        }
    }

    @Test
    void test_ShouldReturnWorkingMessage() throws Exception {
        mockMvc.perform(get("/uploads/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("FileController is working!"));
    }

    @Test
    void debugPath_ShouldReturnPathInfo() throws Exception {
        mockMvc.perform(get("/uploads/debug/test.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Resolved path:")));
    }

    @Test
    void downloadFile_FileNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/uploads/nonexistent.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadFile_FileExists_ShouldReturnWithContentType() throws Exception {
        // Create a test file
        Path testFile = UPLOAD_DIR.resolve("test-image.jpg");
        Files.write(testFile, "test image content".getBytes());

        mockMvc.perform(get("/uploads/test-image.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    void downloadFile_PngFile_ShouldReturnPngContentType() throws Exception {
        Path testFile = UPLOAD_DIR.resolve("test-image.png");
        Files.write(testFile, "test png content".getBytes());

        mockMvc.perform(get("/uploads/test-image.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void uploadFile_ShouldReturnUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test content".getBytes());

        mockMvc.perform(multipart("/uploads").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(org.hamcrest.Matchers.startsWith("/uploads/")));
    }

    @Test
    void uploadFile_EmptyFile_ShouldReturn400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        mockMvc.perform(multipart("/uploads").file(file))
                .andExpect(status().isBadRequest());
    }
}
