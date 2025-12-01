package kr.co.musicart.wnovel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * 관리자 로그인 페이지
     * 템플릿 경로: src/main/resources/templates/admin/auth/login.html
     */
    @GetMapping("/login")
    public String login() {
        return "admin/auth/login";
    }

    /**
     * 관리자 대시보드 (메인 페이지)
     * 템플릿 경로: src/main/resources/templates/admin/main/dashboard.html
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/main/dashboard";
    }
}