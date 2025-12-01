package kr.co.musicart.wnovel.repository;

import kr.co.musicart.wnovel.entity.EpisodeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EpisodeImageRepository extends JpaRepository<EpisodeImage, Long> {

    // 특정 회차의 이미지들을 순서대로 조회
    List<EpisodeImage> findByEpisodeIdOrderBySortOrderAsc(Long episodeId);

    // 특정 회차의 가장 마지막 순서 번호를 조회 (새 이미지 추가 시 순서 결정을 위해)
    @Query("SELECT COALESCE(MAX(ei.sortOrder), -1) FROM EpisodeImage ei WHERE ei.episode.id = :episodeId")
    int findMaxSortOrderByEpisodeId(@Param("episodeId") Long episodeId);

    // 회차의 모든 이미지 삭제 (회차 삭제 시 사용)
    @Modifying
    @Query("DELETE FROM EpisodeImage ei WHERE ei.episode.id = :episodeId")
    void deleteByEpisodeId(@Param("episodeId") Long episodeId);
}