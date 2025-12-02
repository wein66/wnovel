package kr.co.musicart.wnovel.service;

import kr.co.musicart.wnovel.entity.Episode;
import kr.co.musicart.wnovel.entity.EpisodeImage;
import kr.co.musicart.wnovel.entity.Novel;
import kr.co.musicart.wnovel.repository.EpisodeRepository;
import kr.co.musicart.wnovel.repository.EpisodeImageRepository;
import kr.co.musicart.wnovel.repository.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final EpisodeImageRepository episodeImageRepository;
    private final NovelRepository novelRepository;
    private final FileStoreService fileStoreService;
    private final EntityManager entityManager;

    /**
     * 특정 소설의 에피소드 목록 조회 (회차 번호 순)
     * AdminNovelController에서 상세보기 화면을 그릴 때 사용합니다.
     */
    @Transactional(readOnly = true)
    public List<Episode> getEpisodesByNovel(Long novelId) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소설입니다."));
        
        return episodeRepository.findAllByNovelOrderByEpisodeNumberAsc(novel);
    }

    /**
     * 특정 에피소드 조회
     */
    @Transactional(readOnly = true)
    public Episode getEpisode(Long episodeId) {
        return episodeRepository.findById(episodeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다. ID: " + episodeId));
    }

    /**
     * 다음 회차 번호 조회
     * 새 회차 등록 시 자동으로 번호를 매기기 위해 사용합니다.
     */
    @Transactional(readOnly = true)
    public int getNextEpisodeNumber(Long novelId) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소설입니다."));
        
        List<Episode> episodes = episodeRepository.findAllByNovelOrderByEpisodeNumberAsc(novel);
        if (episodes.isEmpty()) {
            return 1;
        }
        return episodes.get(episodes.size() - 1).getEpisodeNumber() + 1;
    }

    /**
     * 에피소드 생성 (HTML 파싱 포함)
     * Summernote 본문에 포함된 이미지 태그를 분석하여 DB에 이미지 정보를 저장합니다.
     */
    public Long createEpisode(Long novelId, String title, int episodeNumber, int requiredPoint, String content) {
        Novel novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소설입니다."));

        // [로직 변경] 제목이 비어있을 경우 처리
        if (title == null || title.trim().isEmpty()) {
            List<Episode> episodes = episodeRepository.findAllByNovelOrderByEpisodeNumberAsc(novel);
            
            if (!episodes.isEmpty()) {
                // 이전 회차가 있으면: 이전 회차의 제목을 그대로 사용 (예: "프롤로그" -> "프롤로그")
                title = episodes.get(episodes.size() - 1).getTitle();
            } else {
                // [수정] 첫 회차라면: 번호 등을 붙이지 않고 "소설 제목" 그대로 저장
                title = novel.getTitle();
            }
        }    

        Episode episode = new Episode();
        episode.setNovel(novel);
        episode.setTitle(title);
        episode.setEpisodeNumber(episodeNumber);
        episode.setRequiredPoint(requiredPoint);
        episode.setContent(content);

        // 1. 에피소드 먼저 저장 (ID 생성)
        episodeRepository.save(episode);

        // 2. 본문(content)에서 이미지 URL 추출하여 EpisodeImage 엔티티 생성
        List<String> imageUrls = extractImageUrls(content);
        
        int sortOrder = 0;
        for (String url : imageUrls) {
            // 파일명 추출 (URL의 마지막 부분)
            String originalFileName = url.substring(url.lastIndexOf("/") + 1);
            
            // EpisodeImage 생성 및 연결 (파일 사이즈는 0으로 임시 처리하거나 File 객체로 확인 가능하나 여기선 0L)
            EpisodeImage image = new EpisodeImage(episode, url, originalFileName, 0L, sortOrder++);
            episodeImageRepository.save(image);
        }

        return episode.getId();
    }

    /**
     * 에피소드 수정
     * 본문 내용이 변경되면, 기존 이미지 정보를 모두 지우고 새로 파싱하여 저장합니다.
     */
    @Transactional
    public void updateEpisode(Long episodeId, String title, int requiredPoint, String content) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다. ID: " + episodeId));

        // 1. 텍스트 정보 업데이트
        episode.setTitle(title);
        episode.setRequiredPoint(requiredPoint);

        // 2. 본문(content)이 변경되었는지 확인하고 이미지 정보 업데이트
        if (!episode.getContent().equals(content)) {
            episode.setContent(content);

            // 기존 이미지 정보 가져오기 (파일 삭제용)
            List<EpisodeImage> oldImages = new ArrayList<>(episode.getImages());

            // 새로운 이미지 URL 목록 추출
            List<String> newImageUrls = extractImageUrls(content);

            // 기존 Episode와 EpisodeImage의 연관관계 제거 및 DB에서 삭제 (orphanRemoval=true)
            episode.getImages().clear();
            entityManager.flush(); // DB에 clear() 연산을 즉시 반영

            // 새로운 이미지 정보 추가
            int sortOrder = 0;
            for (String url : newImageUrls) {
                String originalFileName = url.substring(url.lastIndexOf("/") + 1);
                EpisodeImage newImage = new EpisodeImage(episode, url, originalFileName, 0L, sortOrder++);
                episode.addImage(newImage); // 연관관계 편의 메서드 사용
            }

            // 더 이상 사용되지 않는 기존 이미지 파일 삭제
            for (EpisodeImage oldImage : oldImages) {
                // 새 이미지 목록에 없는 경우에만 파일 삭제
                if (!newImageUrls.contains(oldImage.getImageUrl())) {
                    fileStoreService.deleteFile(oldImage.getImageUrl());
                }
            }
        }
        // @Transactional에 의해 메서드 종료 시 변경된 내용이 DB에 반영됩니다.
    }

    /**
     * 이미지 개별 삭제
     */
    public void deleteImage(Long imageId) {
        EpisodeImage image = episodeImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다."));
        
        // 파일 삭제
        fileStoreService.deleteFile(image.getImageUrl());
        
        // DB 삭제
        episodeImageRepository.delete(image);
    }

    /**
     * 회차 전체 삭제
     */
    @Transactional
    public void deleteEpisode(Long episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));
        
        // 1. 연관된 모든 이미지 파일들을 먼저 삭제
        // episode.getImages()를 통해 LAZY 로딩된 이미지 목록을 가져옴
        for (EpisodeImage image : episode.getImages()) {
            fileStoreService.deleteFile(image.getImageUrl());
        }
        
        // 2. 에피소드 엔티티 삭제
        // CascadeType.ALL 설정에 의해 연관된 EpisodeImage 레코드도 DB에서 함께 삭제됨
        episodeRepository.delete(episode);
    }

    /**
     * [Legacy] 이미지 직접 업로드 (필요 시 사용)
     */
    public void uploadImages(Long novelId, Long episodeId, List<MultipartFile> files) throws IOException {
         Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));
        
        int currentMaxOrder = episodeImageRepository.findMaxSortOrderByEpisodeId(episodeId);
        
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String storedPath = fileStoreService.storeEpisodeImage(file, novelId, episodeId);
            if (storedPath != null) {
                currentMaxOrder++;
                EpisodeImage image = new EpisodeImage(episode, storedPath, file.getOriginalFilename(), file.getSize(), currentMaxOrder);
                episodeImageRepository.save(image);
            }
        }
    }

    // HTML 본문에서 <img src="..."> 의 URL을 추출하는 헬퍼 메서드
    private List<String> extractImageUrls(String content) {
        List<String> urls = new ArrayList<>();
        // 간단한 정규식 예시 (img 태그의 src 속성 추출)
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }
}