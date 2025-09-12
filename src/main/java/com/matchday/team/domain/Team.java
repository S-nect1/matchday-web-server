package com.matchday.team.domain;

import com.matchday.global.entity.BaseEntity;
import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.enums.GroupGender;
import com.matchday.team.domain.enums.TeamType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team")
@NoArgsConstructor
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TeamType type;

    @Enumerated(EnumType.STRING)
    private City city;
    @Enumerated(EnumType.STRING)
    private District district;
    
    private String uniformColorHex;
    
    private Boolean hasBall = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private GroupGender gender;
    
    @Column(name = "member_limit", nullable = false)
    private Integer memberLimit = 0;
    
    @Column(nullable = false, unique = true, length = 6)
    private String inviteCode;

    @Column(nullable = false)
    private String bankName;
    @Column(nullable = false)
    private String bankAccount;

    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    private Integer statsWins = 0;
    private Integer statsDraws = 0;
    private Integer statsLosses = 0;

}