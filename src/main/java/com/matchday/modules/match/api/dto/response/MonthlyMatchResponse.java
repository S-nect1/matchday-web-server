package com.matchday.modules.match.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyMatchResponse {
    
    private Map<Integer, List<Long>> dailyMatches;
    
    public static MonthlyMatchResponse of(Map<Integer, List<Long>> dailyMatches) {
        return MonthlyMatchResponse.builder()
                .dailyMatches(dailyMatches)
                .build();
    }
}