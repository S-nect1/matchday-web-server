package com.matchday.modules.team.api.dto.dto.response;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.team.domain.Team;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamType;

public record TeamResponse(
    Long id,
    String name,
    String description,
    TeamType type,
    City city,
    District district,
    String uniformColorHex,
    Boolean hasBall,
    GroupGender gender,
    Integer memberLimit,
    String inviteCode,
    String bankName,
    String bankAccount,
    String profileImageUrl,
    Integer memberCount
) {
    public static TeamResponse from(Team team, Integer memberCount) {
        return new TeamResponse(
            team.getId(),
            team.getName(),
            team.getDescription(),
            team.getType(),
            team.getCity(),
            team.getDistrict(),
            team.getUniformColorHex(),
            team.getHasBall(),
            team.getGender(),
            team.getMemberLimit(),
            team.getInviteCode(),
            team.getBankName(),
            team.getBankAccount(),
            team.getProfileImageUrl(),
            memberCount
        );
    }
}