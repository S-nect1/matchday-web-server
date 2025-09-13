package com.matchday.review.domain;

import com.matchday.global.entity.BaseEntity;
import com.matchday.match.domain.Match;
import jakarta.persistence.*;

@Entity
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Match match;

    @Column(nullable = false)
    private Integer rating;
    private String content;
}
