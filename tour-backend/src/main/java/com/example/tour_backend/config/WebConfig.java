package com.example.tour_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** 경로 요청이 들어오면 실제 uploads 폴더에서 파일 제공
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); // 서버 루트 기준 상대 경로
    }
}// 파일 업로드 (이미지 게시물에 보이게 설정)