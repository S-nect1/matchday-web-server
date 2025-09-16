package com.matchday.modules.match.api.dto.request;

import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.SportsType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class MatchSearchRequest {
    
    private SportsType sportsType;
    private MatchSize matchSize;
    private LocalDate startDate;
    private LocalDate endDate;
    private String keyword;
    private int page = 0;
    private int size = 20;
    private String sort = "createdAt";
    private String direction = "desc";
}