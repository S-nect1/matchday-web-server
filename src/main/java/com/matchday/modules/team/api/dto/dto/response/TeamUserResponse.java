package com.matchday.modules.team.api.dto.dto.response;

import com.matchday.modules.team.domain.TeamUser;
import com.matchday.modules.team.domain.enums.TeamRole;
import com.matchday.modules.user.domain.User;

public record TeamUserResponse(
    Long id,
    TeamRole role,
    Integer backNumber,
    UserSummary user
) {
    public static TeamUserResponse from(TeamUser teamUser) {
        return new TeamUserResponse(
            teamUser.getId(),
            teamUser.getRole(),
            teamUser.getBackNumber(),
            UserSummary.from(teamUser.getUser())
        );
    }
    
    public record UserSummary(
        Long id,
        String name,
        String email
    ) {
        public static UserSummary from(User user) {
            return new UserSummary(
                user.getId(),
                user.getName(),
                user.getEmail()
            );
        }
    }
}