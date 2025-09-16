package com.matchday.modules.match.api.dto.request;

import com.matchday.modules.match.domain.enums.MatchSize;
import com.matchday.modules.match.domain.enums.SportsType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MatchUpdateRequest {
    
    private String sportsType;
    
    private MatchSize matchSize;
    
    @Future(message = "경기일시는 현재보다 미래여야 합니다.")
    private LocalDateTime matchDateTime;
    
    private LocalDateTime endDateTime;
    
    @Valid
    private MatchLocation matchLocation;
    
    @Min(value = 0, message = "대관료는 0원 이상이어야 합니다.")
    private Integer fee;
    
    @Size(max = 7, message = "상의유니폼색깔은 7자를 초과할 수 없습니다.")
    private String uniformColor;
    
    private Boolean hasBall;
    
    @Getter
    @NoArgsConstructor
    public static class MatchLocation {
        private String zipCode;
        
        private String address;
        
        private String detailAddress;
    }
    
    public SportsType getSportsTypeEnum() {
        try {
            return SportsType.valueOf(sportsType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 스포츠 종목입니다: " + sportsType);
        }
    }
}