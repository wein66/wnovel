package kr.co.musicart.wnovel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저 접근 URL: /novel-img/**
        // 실제 파일 위치: file:///C:/project/uploads/novel/ (윈도우) 또는 file:/project/uploads/novel/ (맥/리눅스)
        
        // 경로 끝에 슬래시(/)가 반드시 있어야 함
        String resourceLocation = "file:///" + new java.io.File(uploadDir).getAbsolutePath() + "/";

        registry.addResourceHandler("/novel-img/**")
                .addResourceLocations(resourceLocation + "novel/");
        
        // static 폴더 설정 (기본값)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}