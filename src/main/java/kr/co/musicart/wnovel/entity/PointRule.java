package kr.co.musicart.wnovel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point_rule")
@Getter
@Setter
@NoArgsConstructor
public class PointRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novel novel;
    
    @Column(nullable = false)
    private int startEpisodeNumber;

    @Column(nullable = false)
    private int endEpisodeNumber;
    
    @Column(nullable = false)
    private int requiredPoint;
}