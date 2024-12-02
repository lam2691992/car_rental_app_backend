package com.noob.example.controller;

import com.noob.example.entity.User;
import com.noob.example.enums.UserRole;
import com.noob.example.services.export.UserExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserExportService userExportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportUsers() {
        try {
            // Giả lập danh sách người dùng
            List<User> users = Arrays.asList(
                    new User(1L, "John Doe", "john@example.com", "password123", UserRole.ADMIN),
                    new User(2L, "Jane Smith", "jane@example.com", "password456", UserRole.CUSTOMER)
            );

            // Đường dẫn xuất file tạm
            String tempFile = System.getProperty("java.io.tmpdir") + "users_export.xlsx";

            // Xuất dữ liệu
            logger.info("Starting export process");
            userExportService.exportUsersToExcel(users, tempFile);
            logger.info("Export process completed, reading file...");

            // Đọc file Excel
            Path filePath = Paths.get(tempFile);
            if (!Files.exists(filePath)) {
                logger.error("Exported file not found at: {}", tempFile);
                throw new RuntimeException("Exported file not found");
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            logger.info("File read successfully, preparing response...");

            // Trả file về phía client
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users_export.xlsx\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileBytes);

        } catch (Exception e) {
            logger.error("Error during export: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(("Lỗi khi xuất dữ liệu: " + e.getMessage()).getBytes());
        }
    }
}
