package com.matchday.team.dto.response;

import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.Team;
import com.matchday.team.domain.enums.GroupGender;
import com.matchday.team.domain.enums.TeamType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "팀 목록 응답")
public class TeamListResponse {

    @Schema(description = "팀 ID", example = "1")
    private Long id;

    @Schema(description = "팀명", example = "FC 매치데이")
    private String name;

    @Schema(description = "팀 설명", example = "강남 지역 풋살팀입니다")
    private String description;

    @Schema(description = "팀 유형", example = "CLUB")
    private TeamType type;

    @Schema(description = "활동 지역 (시/도)", example = "SEOUL")
    private City city;

    @Schema(description = "활동 지역 (구/군)", example = "GANGNAM_GU")
    private District district;

    @Schema(description = "성별", example = "MIXED")
    private GroupGender gender;

    @Schema(description = "유니폼 색상", example = "FF0000")
    private String uniformColorHex;

    @Schema(description = "멤버 제한", example = "25")
    private Integer memberLimit;

    @Schema(description = "공 보유 여부", example = "true")
    private Boolean hasBall;

    @Schema(description = "팀 프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "팀 생성일", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    public static TeamListResponse from(Team team) {
        return TeamListResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .type(team.getType())
                .city(team.getCity())
                .district(team.getDistrict())
                .gender(team.getGender())
                .uniformColorHex(team.getUniformColorHex())
                .memberLimit(team.getMemberLimit())
                .hasBall(team.getHasBall())
                .profileImageUrl(team.getProfileImageUrl())
                .createdAt(team.getCreatedDate())
                .build();
    }
}