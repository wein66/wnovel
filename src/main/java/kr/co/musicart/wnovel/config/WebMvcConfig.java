package kr.co.musicart.wnovel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
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

    /**
     * 특정 패키지 하위의 컨트롤러에 URL 접두사(/admin)를 자동으로 추가합니다.
     * 예: AdminNovelController의 @RequestMapping("/novel") -> /admin/novel
     */
    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        // kr.co.musicart.wnovel.controller 패키지 내의 Admin*Controller 들에만 적용
        configurer.addPathPrefix("/admin", c ->
                c.isAnnotationPresent(org.springframework.stereotype.Controller.class) &&
                c.getPackage().getName().equals("kr.co.musicart.wnovel.controller") &&
                c.getSimpleName().startsWith("Admin")
        );
    }
}