package kr.co.musicart.wnovel.repository;

import kr.co.musicart.wnovel.entity.Episode;
import kr.co.musicart.wnovel.entity.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    List<Episode> findAllByNovelOrderByEpisodeNumberAsc(Novel novel);
    Optional<Episode> findByNovelAndEpisodeNumber(Novel novel, int episodeNumber);
}