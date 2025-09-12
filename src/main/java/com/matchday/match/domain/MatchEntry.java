package com.matchday.match.domain;

import com.matchday.global.entity.BaseEntity;
import com.matchday.team.domain.Team;
import jakarta.persistence.*;

@Entity
public class MatchEntry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;
}
