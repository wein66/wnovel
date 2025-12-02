package kr.co.musicart.wnovel.controller.admin;

import kr.co.musicart.wnovel.entity.Novel;
import kr.co.musicart.wnovel.repository.NovelRepository;
import kr.co.musicart.wnovel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMainController {

    private final UserRepository userRepository;
    private final NovelRepository novelRepository;

    /**
     * 관리자 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        return "admin/auth/login";
    }

    // Spring Security는 로그인 실패 시 기본적으로 /login?error 로 리다이렉트합니다.
    // 따라서 /admin/login?error 경로도 동일한 로그인 페이지를 보여주도록 처리합니다.
    // 별도의 처리가 필요 없다면 위 /login 메서드 하나로도 동작할 수 있습니다.

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalUsers = userRepository.count();
        long totalNovels = novelRepository.count();
        List<Novel> recentNovels = novelRepository.findByOrderByCreatedAtDesc(PageRequest.of(0, 5));

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalNovels", totalNovels);
        model.addAttribute("recentNovels", recentNovels);

        return "admin/main/dashboard";
    }
}