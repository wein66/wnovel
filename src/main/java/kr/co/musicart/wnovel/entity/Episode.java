package kr.co.musicart.wnovel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Episode 엔티티: 소설의 각 회차(편) 정보를 저장합니다.
 * [수정] 1:N 관계 적용으로 imageUrls 필드를 images 리스트로 변경했습니다.
 */
@Entity
@Table(name = "episode")
@Getter
@Setter
@NoArgsConstructor
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속된 소설
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novel novel;

    // 회차 제목
    @Column(nullable = false, length = 100)
    private String title;

    // 회차 번호
    @Column(nullable = false)
    private int episodeNumber;

    // 본문 내용 (HTML 또는 Markdown)
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    // [수정됨] 기존 String imageUrls -> List<EpisodeImage>로 변경
    // mappedBy: EpisodeImage의 'episode' 필드에 매핑됨
    // cascade = CascadeType.ALL: 에피소드 저장/삭제 시 이미지들도 함께 저장/삭제
    // orphanRemoval = true: 리스트에서 이미지를 빼면 DB에서도 삭제됨
    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EpisodeImage> images = new ArrayList<>();

    // 이 회차를 열람할 때 차감되는 포인트
    @Column(nullable = false)
    private int requiredPoint = 0; 
    
    // 이 회차가 발행된 날짜
    private LocalDateTime publishedAt; 

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void onUpdate() {
        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    // [추가됨] 연관관계 편의 메서드
    // 에피소드에 이미지를 추가할 때, 양쪽(Episode, EpisodeImage) 모두에 값을 세팅해줍니다.
    public void addImage(EpisodeImage image) {
        this.images.add(image);
        image.setEpisode(this);
    }
}