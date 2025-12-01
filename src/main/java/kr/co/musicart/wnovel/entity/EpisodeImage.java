package kr.co.musicart.wnovel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "episode_image")
@Getter
@Setter
@NoArgsConstructor
public class EpisodeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속된 회차
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    // 파일 경로 (예: /novel-img/1/5/uuid_abc.jpg)
    @Column(nullable = false, length = 255)
    private String imageUrl;

    // 원본 파일명 (사용자가 업로드한 이름)
    @Column(nullable = false, length = 255)
    private String originalFileName;

    // 파일 크기 (바이트 단위)
    private long fileSize;

    // 노출 순서 (0부터 시작)
    @Column(nullable = false)
    private int sortOrder;

    // 생성자 편의 메서드
    public EpisodeImage(Episode episode, String imageUrl, String originalFileName, long fileSize, int sortOrder) {
        this.episode = episode;
        this.imageUrl = imageUrl;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.sortOrder = sortOrder;
    }
}