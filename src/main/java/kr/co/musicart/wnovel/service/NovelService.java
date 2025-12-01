package kr.co.musicart.wnovel.service;

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
    private final FileStoreService fileStoreService; // 파일 저장 서비스 주입

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
    
    // 추후 수정, 삭제, 포인트 규칙 설정 메서드 추가 예정
}