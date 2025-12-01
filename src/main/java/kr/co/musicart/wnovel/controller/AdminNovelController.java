package kr.co.musicart.wnovel.controller;

import kr.co.musicart.wnovel.entity.Episode;
import kr.co.musicart.wnovel.entity.Novel;
import kr.co.musicart.wnovel.service.EpisodeService;
import kr.co.musicart.wnovel.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/novel")
@RequiredArgsConstructor
public class AdminNovelController {

    private final NovelService novelService;
    private final EpisodeService episodeService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Novel> novels = novelService.getNovels();
        model.addAttribute("novels", novels);
        model.addAttribute("menu", "novel");
        return "admin/novel/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("categories", Novel.Category.values());
        model.addAttribute("menu", "novel");
        return "admin/novel/form";
    }

    @PostMapping("/create")
    public String create(@RequestParam("title") String title,
                         @RequestParam("category") Novel.Category category,
                         @RequestParam("description") String description,
                         @RequestParam("status") Novel.Status status,
                         @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                         @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        
        novelService.createNovel(title, category, description, status, coverImage, userDetails.getUsername());
        return "redirect:/admin/novel/list";
    }

    /**
     * [수정] 소설 상세 및 회차 목록 페이지: @PathVariable을 명확히 정의
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Novel novel = novelService.getNovel(id);
        List<Episode> episodes = episodeService.getEpisodesByNovel(id);

        model.addAttribute("novel", novel);
        model.addAttribute("episodes", episodes);
        model.addAttribute("menu", "novel");
        
        return "admin/novel/detail";
    }
}