package com.matchday.team.domain;

import com.matchday.global.entity.BaseEntity;
import com.matchday.team.domain.enums.TeamRole;
import com.matchday.user.domain.User;
import jakarta.persistence.*;

@Entity
public class TeamUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private TeamRole role;

    private Integer score;
    private Integer backNumber;
}
