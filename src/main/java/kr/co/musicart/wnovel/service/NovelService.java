package kr.co.musicart.wnovel.service;

import kr.co.musicart.wnovel.entity.Episode;
import kr.co.musicart.wnovel.entity.Novel;
import kr.co.musicart.wnovel.entity.User;
import kr.co.musicart.wnovel.repository.NovelRepository;
import kr.co.musicart.wnovel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NovelService {

    private final NovelRepository novelRepository;
    private final UserRepository userRepository;
    private final FileStoreService fileStoreService;
    private final EpisodeService episodeService;

    /**
     * 소설 생성 (파일 업로드 포함)
     */
    @Transactional
    public Long createNovel(String title, Novel.Category category, String description, 
                          Novel.Status status, MultipartFile coverImage, String username) throws IOException {
        
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Novel novel = new Novel();
        novel.setTitle(title);
        novel.setCategory(category);
        novel.setDescription(description);
        novel.setAuthor(author);
        novel.setStatus(status); // 폼에서 선택한 상태값 적용

        // 1. 소설 정보 먼저 저장 (ID 생성을 위해)
        novelRepository.save(novel);

        // 2. 표지 이미지 저장 로직
        if (coverImage != null && !coverImage.isEmpty()) {
            // ID가 생성된 후 이미지를 저장해야 폴더명에 ID를 쓸 수 있음
            String coverUrl = fileStoreService.storeCoverImage(coverImage, novel.getId());
            novel.setCoverImageUrl(coverUrl);
        }

        // 3. 업데이트된 소설 정보 저장 (표지 URL 포함)
        // JPA의 영속성 컨텍스트 덕분에 따로 save()를 호출하지 않아도 되지만, 
        // 명시적으로 업데이트 로직을 보여줄 수 있습니다. (여기서는 @Transactional로 인해 자동 저장됨)
        // novelRepository.save(novel);

        return novel.getId();
    }

    /**
     * 전체 소설 목록 조회 (최신순)
     */
    public List<Novel> getNovels() {
        return novelRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * 소설 상세 조회
     */
    public Novel getNovel(Long id) {
        return novelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소설입니다."));
    }

    /**
     * 소설 정보 수정 (표지 이미지 포함)
     */
    @Transactional
    public void updateNovel(Long novelId, String title, Novel.Category category, String description,
                            Novel.Status status, MultipartFile newCoverImage) throws IOException {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소설입니다. ID: " + novelId));

        // 1. 텍스트 정보 업데이트
        novel.setTitle(title);
        novel.setCategory(category);
        novel.setDescription(description);
        novel.setStatus(status);

        // 2. 표지 이미지 업데이트 (새 파일이 있을 경우)
        if (newCoverImage != null && !newCoverImage.isEmpty()) {
            // 기존 이미지가 있으면 삭제
            if (novel.getCoverImageUrl() != null && !novel.getCoverImageUrl().isEmpty()) {
                fileStoreService.deleteFile(novel.getCoverImageUrl());
            }
            // 새 이미지 저장 및 URL 업데이트
            String newCoverUrl = fileStoreService.storeCoverImage(newCoverImage, novel.getId());
            novel.setCoverImageUrl(newCoverUrl);
        }

        // @Transactional에 의해 변경 감지(dirty checking)로 자동 업데이트됩니다.
    }

    /**
     * 소설 삭제 (연관된 회차, 이미지 파일 모두 삭제)
     */
    @Transactional
    public void deleteNovel(Long novelId) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소설입니다. ID: " + novelId));

        // 1. 연관된 모든 회차 삭제 (회차 삭제 시 내부적으로 이미지 파일도 삭제됨)
        List<Episode> episodes = episodeService.getEpisodesByNovel(novelId);
        for (Episode episode : episodes) {
            episodeService.deleteEpisode(episode.getId());
        }

        // 2. 소설 표지 이미지 파일 삭제
        if (novel.getCoverImageUrl() != null && !novel.getCoverImageUrl().isEmpty()) {
            fileStoreService.deleteFile(novel.getCoverImageUrl());
        }

        // 3. 소설 엔티티 삭제
        novelRepository.delete(novel);
    }
}