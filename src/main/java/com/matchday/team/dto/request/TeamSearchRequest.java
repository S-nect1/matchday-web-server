package com.matchday.team.dto.request;

import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.enums.GroupGender;
import com.matchday.team.domain.enums.TeamType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "팀 검색 요청")
public class TeamSearchRequest {

    @Schema(description = "시/도", example = "SEOUL")
    private City city;

    @Schema(description = "구/군", example = "GANGNAM_GU")
    private District district;

    @Schema(description = "팀 유형", example = "CLUB", allowableValues = {"SMALL_GROUP", "CLUB", "COMMUNITY"})
    private TeamType type;

    @Schema(description = "성별", example = "MIXED", allowableValues = {"MALE", "FEMALE", "MIXED"})
    private GroupGender gender;

    @Schema(description = "팀명 검색 키워드", example = "FC")
    private String keyword;

    @Schema(description = "페이지 번호", example = "0", defaultValue = "0")
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    private int page = 0;

    @Schema(description = "페이지 크기", example = "20", defaultValue = "20")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    private int size = 20;

    @Schema(description = "정렬 기준", example = "createdAt", defaultValue = "createdAt")
    private String sort = "createdAt";

    @Schema(description = "정렬 방향", example = "desc", allowableValues = {"asc", "desc"}, defaultValue = "desc")
    private String direction = "desc";
}