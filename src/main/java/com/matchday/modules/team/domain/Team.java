package com.matchday.modules.team.domain;

import com.matchday.common.entity.BaseEntity;
import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.common.entity.enums.ResponseCode;
import com.matchday.modules.team.domain.enums.GroupGender;
import com.matchday.modules.team.domain.enums.TeamType;
import com.matchday.modules.team.exception.TeamControllerAdvice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Random;

@Getter
@Entity
@Table(name = "team")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamType type;

    @Enumerated(EnumType.STRING)
    private City city;
    @Enumerated(EnumType.STRING)
    private District district;
    
    private String uniformColorHex;
    
    private Boolean hasBall = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupGender gender;
    
    @Column(nullable = false)
    private Integer memberLimit = 0;
    
    @Column(nullable = false, unique = true, length = 6)
    private String inviteCode;

    @Column(nullable = false)
    private String bankName;
    @Column(nullable = false)
    private String bankAccount;

    private String profileImageUrl;


    // TODO: 전적과 함께 추후 디벨롭
//    @Enumerated(EnumType.STRING)
//    private TeamLevel teamLevel;

//    private Integer statsWins = 0;
//    private Integer statsDraws = 0;
//    private Integer statsLosses = 0;


    // 팀 생성 정적 팩토리 메서드
    public static Team createTeam(String name, String description, TeamType type, 
                                 City city, District district, String uniformColorHex,
                                 Boolean hasBall, GroupGender gender, Integer memberLimit,
                                 String bankName, String bankAccount, String profileImageUrl) {
        validateTeamCreation(name, type, gender, bankName, bankAccount);
        
        Team team = new Team();
        team.name = name;
        team.description = description;
        team.type = type;
        team.city = city;
        team.district = district;
        team.uniformColorHex = uniformColorHex;
        team.hasBall = hasBall != null ? hasBall : false;
        team.gender = gender;
        team.memberLimit = memberLimit != null ? memberLimit : 0;
        team.inviteCode = generateInviteCode();
        team.bankName = bankName;
        team.bankAccount = bankAccount;
        team.profileImageUrl = profileImageUrl;
        
        return team;
    }

    
    // 개별 필드 업데이트 메서드
    public void updateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_NAME_REQUIRED);
        }
        if (name.length() > 100) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_NAME_TOO_LONG);
        }
        this.name = name;
    }
    
    public void updateDescription(String description) {
        this.description = description;
    }
    
    public void updateActivityArea(City city, District district) {
        this.city = city;
        this.district = district;
    }
    
    public void updateUniformColor(String uniformColorHex) {
        this.uniformColorHex = uniformColorHex;
    }
    
    public void updateHasBall(Boolean hasBall) {
        this.hasBall = hasBall != null ? hasBall : false;
    }
    
    public void updateMemberLimit(Integer memberLimit) {
        if (memberLimit != null && memberLimit < 0) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_MEMBER_LIMIT_INVALID);
        }
        this.memberLimit = memberLimit != null ? memberLimit : 0;
    }
    
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 계좌 정보 수정
    public void updateBankInfo(String bankName, String bankAccount) {
        validateBankInfo(bankName, bankAccount);
        this.bankName = bankName;
        this.bankAccount = bankAccount;
    }

    // 초대코드 재생성
    public void regenerateInviteCode() {
        this.inviteCode = generateInviteCode();
    }

    // 멤버 제한 확인
    public boolean canAddMember(int currentMemberCount) {
        return memberLimit == 0 || currentMemberCount < memberLimit;
    }

    // 검증 메서드들
    private static void validateTeamCreation(String name, TeamType type, GroupGender gender, 
                                           String bankName, String bankAccount) {
        if (name == null || name.trim().isEmpty()) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_NAME_REQUIRED);
        }
        if (name.length() > 100) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_NAME_TOO_LONG);
        }
        if (type == null) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_TYPE_REQUIRED);
        }
        if (gender == null) {
            throw new TeamControllerAdvice(ResponseCode.TEAM_GENDER_REQUIRED);
        }
        validateBankInfo(bankName, bankAccount);
    }

    // TODO: 실제 계좌 확인 필요 없나?
    private static void validateBankInfo(String bankName, String bankAccount) {
        if (bankName == null || bankName.trim().isEmpty()) {
            throw new TeamControllerAdvice(ResponseCode.BANK_NAME_REQUIRED);
        }
        if (bankAccount == null || bankAccount.trim().isEmpty()) {
            throw new TeamControllerAdvice(ResponseCode.BANK_ACCOUNT_REQUIRED);
        }
    }

    // 6자리 초대코드 생성
    private static String generateInviteCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }


    // TODO: 경기 결과 업데이트(추후 디벨롭)
//    public void updateMatchResult(boolean isWin, boolean isDraw) {
//        if (isWin) {
//            this.statsWins++;
//        } else if (isDraw) {
//            this.statsDraws++;
//        } else {
//            this.statsLosses++;
//        }
//    }
    // 팀 전적 조회
//    public int getTotalMatches() {
//        return statsWins + statsDraws + statsLosses;
//    }
    // 승률 계산
//    public double getWinRate() {
//        int totalMatches = getTotalMatches();
//        return totalMatches == 0 ? 0.0 : (double) statsWins / totalMatches * 100;
//    }
}