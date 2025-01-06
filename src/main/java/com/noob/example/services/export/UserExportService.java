package com.noob.example.services.export;

import com.noob.example.entity.User;
import com.noob.example.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class UserExportService {

    @Autowired
    private UserRepository userRepository; // UserRepository để truy cập dữ liệu

    public ByteArrayInputStream exportUsersToExcel() throws IOException {
        List<User> userList = userRepository.findAll(); // Lấy danh sách User từ database
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Tạo tiêu đề
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Password");
        headerRow.createCell(4).setCellValue("User Role");

        // Thêm dữ liệu
        int rowNum = 1;
        for (User user : userList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getName());
            row.createCell(2).setCellValue(user.getEmail());
            row.createCell(3).setCellValue(user.getPassword());
            row.createCell(4).setCellValue(user.getUserRole().name());
        }

        // Ghi vào ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}