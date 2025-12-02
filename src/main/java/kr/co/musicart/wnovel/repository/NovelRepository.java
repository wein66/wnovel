package kr.co.musicart.wnovel.repository;

import kr.co.musicart.wnovel.entity.Novel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NovelRepository extends JpaRepository<Novel, Long> {
    List<Novel> findByOrderByCreatedAtDesc(Pageable pageable);
}