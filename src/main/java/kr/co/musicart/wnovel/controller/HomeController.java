package kr.co.musicart.wnovel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // 루트(/)로 접속하면 관리자 로그인 페이지로 리다이렉트
        return "redirect:/admin/login";
    }
}