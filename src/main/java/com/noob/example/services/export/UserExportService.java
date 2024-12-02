package com.noob.example.services.export;

import com.noob.example.entity.User;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class UserExportService {

    private static final Logger logger = LoggerFactory.getLogger(UserExportService.class);

    public void exportUsersToExcel(List<User> users, String outputPath) {
        try (InputStream template = getClass().getResourceAsStream("/templates/users_template.xlsx");
             FileOutputStream outputStream = new FileOutputStream(outputPath)) {

            if (template == null) {
                throw new RuntimeException("Template file not found: /templates/users_template.xlsx");
            }

            logger.info("Template loaded successfully");
            logger.info("Exporting data to: {}", outputPath);

            // Chuẩn bị dữ liệu cho JXLS
            Context context = new Context();
            context.putVar("users", users);

            // Sử dụng JXLS để xử lý
            JxlsHelper.getInstance().processTemplate(template, outputStream, context);

            logger.info("File exported successfully");
        } catch (Exception e) {
            logger.error("Error exporting users to Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Something went wrong", e);
        }
    }
}

