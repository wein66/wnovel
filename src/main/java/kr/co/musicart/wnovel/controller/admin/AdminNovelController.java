package kr.co.musicart.wnovel.controller.admin;

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
@RequestMapping("/novel")
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
        // [수정] 생성 폼에서도 th:object를 사용하기 위해 비어있는 Novel 객체를 모델에 추가
        model.addAttribute("novel", new Novel());
        model.addAttribute("categories", Novel.Category.values());
        model.addAttribute("menu", "novel");
        return "admin/novel/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Novel novel,
                         @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                         @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        novelService.createNovel(novel.getTitle(),
                novel.getCategory(),
                novel.getDescription(),
                novel.getStatus(),
                coverImage,
                userDetails.getUsername());
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

    /**
     * 소설 수정 폼 페이지
     * 기존 소설 정보를 불러와 폼에 채워줍니다.
     */
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        Novel novel = novelService.getNovel(id);
        model.addAttribute("novel", novel);
        model.addAttribute("categories", Novel.Category.values());
        model.addAttribute("menu", "novel");
        // 생성 폼(form.html)을 수정 폼으로 재활용
        return "admin/novel/form";
    }

    /**
     * 소설 수정 처리
     */
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute Novel novel,
                         @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) throws IOException {
        novelService.updateNovel(id,
                novel.getTitle(),
                novel.getCategory(),
                novel.getDescription(),
                novel.getStatus(), coverImage);
        return "redirect:/admin/novel/detail/" + id;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        novelService.deleteNovel(id);
        return "redirect:/admin/novel/list";
    }
}