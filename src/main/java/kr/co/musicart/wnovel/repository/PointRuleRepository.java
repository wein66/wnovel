package kr.co.musicart.wnovel.repository;

import kr.co.musicart.wnovel.entity.Novel;
import kr.co.musicart.wnovel.entity.PointRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PointRuleRepository extends JpaRepository<PointRule, Long> {
    List<PointRule> findAllByNovel(Novel novel);
}