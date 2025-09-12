package com.matchday.match.domain.enums;

public enum MatchSize {
    FIVE(SportsType.FUTSAL),
    SIX(SportsType.FUTSAL),
    EIGHT(SportsType.SOCCER),
    ELEVEN(SportsType.SOCCER);

    private final SportsType sportsType;

    MatchSize(SportsType sportsType) {
        this.sportsType = sportsType;
    }

    public static MatchSize[] getMatchSizeBySportsType(SportsType type) {
        return java.util.Arrays.stream(values())
                .filter(matchSize -> matchSize.sportsType == type)
                .toArray(MatchSize[]::new);
    }
}
