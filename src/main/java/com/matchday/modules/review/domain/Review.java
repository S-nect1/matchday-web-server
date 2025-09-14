package com.matchday.modules.review.domain;

import com.matchday.common.entity.BaseEntity;
import com.matchday.modules.match.domain.Match;
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
