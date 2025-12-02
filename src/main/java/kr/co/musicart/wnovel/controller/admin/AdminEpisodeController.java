package kr.co.musicart.wnovel.controller.admin;

import kr.co.musicart.wnovel.entity.Episode;
import kr.co.musicart.wnovel.service.EpisodeService;
import kr.co.musicart.wnovel.service.FileStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/episode")
@RequiredArgsConstructor
public class AdminEpisodeController {

    private final EpisodeService episodeService;
    private final FileStoreService fileStoreService;

    /**
     * 회차 등록 폼 페이지
     */
    @GetMapping("/create")
    public String createForm(@RequestParam("novelId") Long novelId, Model model) {
        // 다음 회차 번호 자동 계산 로직 (Service에 위임 가능하지만 여기선 간단히 처리 예시)
        int nextEpisodeNumber = episodeService.getNextEpisodeNumber(novelId);
        
        // [수정] 생성 폼에서도 th:object를 사용하기 위해 비어있는 Episode 객체를 모델에 추가
        Episode episode = new Episode();
        episode.setEpisodeNumber(nextEpisodeNumber);

        model.addAttribute("episode", episode);
        model.addAttribute("novelId", novelId);
        model.addAttribute("menu", "novel");
        return "admin/episode/form";
    }

    /**
     * 회차 등록 처리
     */
    @PostMapping("/create")
    public String create(@RequestParam("novelId") Long novelId,
                         @RequestParam("title") String title,
                         @RequestParam("episodeNumber") int episodeNumber,
                         @RequestParam("requiredPoint") int requiredPoint,
                         @RequestParam("content") String content) {
        
        episodeService.createEpisode(novelId, title, episodeNumber, requiredPoint, content);
        
        return "redirect:/admin/novel/detail/" + novelId;
    }

    /**
     * 회차 수정 폼 페이지
     */
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        Episode episode = episodeService.getEpisode(id);
        model.addAttribute("episode", episode);
        model.addAttribute("novelId", episode.getNovel().getId());
        model.addAttribute("menu", "novel");
        // 생성 폼(form.html)을 수정 폼으로 재활용
        return "admin/episode/form";
    }

    /**
     * 회차 수정 처리
     */
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Long id,
                         @RequestParam("novelId") Long novelId,
                         @RequestParam("title") String title,
                         @RequestParam("requiredPoint") int requiredPoint,
                         @RequestParam("content") String content) {

        episodeService.updateEpisode(id, title, requiredPoint, content);
        return "redirect:/admin/novel/detail/" + novelId;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, @RequestParam("novelId") Long novelId) {
        episodeService.deleteEpisode(id);
        // 삭제 후 해당 소설의 상세 페이지로 리다이렉트
        return "redirect:/admin/novel/detail/" + novelId;
    }

    /**
     * [API] Summernote 이미지 업로드 처리
     * 에디터에서 이미지를 드래그하면 이 API가 호출되어 파일을 저장하고 URL을 반환합니다.
     */
    @PostMapping("/api/upload-image")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file, 
                              @RequestParam("novelId") Long novelId) throws IOException {
        
        // 아직 Episode ID가 생성되기 전이므로 'temp' 또는 '0'으로 폴더를 지정하거나
        // novel 폴더 아래에 바로 저장 후 추후 이동하는 전략을 쓸 수 있습니다.
        // 여기서는 FileStoreService를 사용하여 novelId 폴더 아래 'temp' 또는 공용 폴더에 저장합니다.
        // (FileStoreService의 로직에 따라 episodeId 자리에 0을 넘김)
        return fileStoreService.storeEpisodeImage(file, novelId, 0L);
    }
}