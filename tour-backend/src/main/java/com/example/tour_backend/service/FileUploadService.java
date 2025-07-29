package com.example.tour_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
// 파일 업로드
@Service
public class FileUploadService {

    // application.properties 에서 file.upload.path 값을 주입받음
    @Value("${file.upload.path}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        try {
            // 파일명 중복 방지 위해 UUID 붙이기
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 경로 설정
            Path uploadPath = Paths.get(uploadDir);

            // 경로 없으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 프론트에서 접근할 수 있는 URL 경로 반환
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
