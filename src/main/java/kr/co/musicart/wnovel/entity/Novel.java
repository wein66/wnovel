package kr.co.musicart.wnovel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "novel")
@Getter
@Setter
@NoArgsConstructor
public class Novel {

    public enum Status {
        PUBLISHED("연재중"),
        COMPLETED("완결"),
        DRAFT("임시저장");

        @Getter
        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }
    }

    public enum Category {
        FANTASY("판타지"),
        MUHYEOP("무협"),
        ROMANCE("로맨스"),
        LITERATURE("문학"),
        READER_BOARD("독자게시판");

        @Getter
        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private long viewCount = 0;

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PointRule> pointRules;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}