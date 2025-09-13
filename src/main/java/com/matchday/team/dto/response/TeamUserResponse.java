package com.matchday.team.dto.response;

import com.matchday.team.domain.TeamUser;
import com.matchday.team.domain.enums.TeamRole;
import com.matchday.user.domain.User;

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