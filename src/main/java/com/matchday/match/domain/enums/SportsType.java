package com.matchday.match.domain.enums;

public enum SportsType {
    FUTSAL("풋살"),
    SOCCER("축구");

    private final String koreanName;

    SportsType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public static SportsType fromKoreanName(String koreanName) {
        for (SportsType type : values()) {
            if (type.koreanName.equals(koreanName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("해당하는 매치 유형을 찾을 수 없습니다: " + koreanName);
    }
}
