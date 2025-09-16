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
public class MatchCreateRequest {
    
    @NotNull(message = "팀 ID는 필수입니다.")
    private Long teamId;
    
    @NotBlank(message = "매치종목은 필수입니다.")
    private String sportsType;
    
    @NotNull(message = "인원수는 필수입니다.")
    private MatchSize matchSize;
    
    @NotNull(message = "경기일시는 필수입니다.")
    @Future(message = "경기일시는 현재보다 미래여야 합니다.")
    private LocalDateTime matchDateTime;
    
    @NotNull(message = "종료일시는 필수입니다.")
    private LocalDateTime endDateTime;
    
    @Valid
    @NotNull(message = "매치장소는 필수입니다.")
    private MatchLocation matchLocation;
    
    @Min(value = 0, message = "대관료는 0원 이상이어야 합니다.")
    private Integer fee = 0;
    
    @Size(max = 7, message = "상의유니폼색깔은 7자를 초과할 수 없습니다.")
    private String uniformColor;
    
    @NotNull(message = "공보유유무는 필수입니다.")
    private Boolean hasBall;
    
//    @Size(max = 500, message = "기타참고사항은 500자를 초과할 수 없습니다.")
//    private String notes;
    
    @Getter
    @NoArgsConstructor
    public static class MatchLocation {
        @NotBlank(message = "우편번호는 필수입니다.")
        private String zipCode;
        
        @NotBlank(message = "주소는 필수입니다.")
        private String address;
        
        @NotBlank(message = "상세주소는 필수입니다.")
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