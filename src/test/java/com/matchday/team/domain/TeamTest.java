package com.matchday.team.domain;

import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.enums.GroupGender;
import com.matchday.team.domain.enums.TeamType;
import com.matchday.team.exception.advice.TeamControllerAdvice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TeamTest {

    @Test
    @DisplayName("팀 생성 시 모든 필수 정보가 올바르게 설정된다")
    void 팀_생성_성공() {
        // given
        String name = "FC Barcelona";
        String description = "프로 축구팀";
        TeamType type = TeamType.CLUB;
        City city = City.SEOUL;
        District district = District.SEOUL_GANGNAM;
        String uniformColorHex = "#FF0000";
        Boolean hasBall = true;
        GroupGender gender = GroupGender.MALE;
        Integer memberLimit = 20;
        String bankName = "국민은행";
        String bankAccount = "123-456-789";
        String profileImageUrl = "https://example.com/image.jpg";

        // when
        Team team = Team.createTeam(name, description, type, city, district, 
                                   uniformColorHex, hasBall, gender, memberLimit,
                                   bankName, bankAccount, profileImageUrl);

        // then
        assertThat(team.getName()).isEqualTo(name);
        assertThat(team.getDescription()).isEqualTo(description);
        assertThat(team.getType()).isEqualTo(type);
        assertThat(team.getCity()).isEqualTo(city);
        assertThat(team.getDistrict()).isEqualTo(district);
        assertThat(team.getUniformColorHex()).isEqualTo(uniformColorHex);
        assertThat(team.getHasBall()).isEqualTo(hasBall);
        assertThat(team.getGender()).isEqualTo(gender);
        assertThat(team.getMemberLimit()).isEqualTo(memberLimit);
        assertThat(team.getBankName()).isEqualTo(bankName);
        assertThat(team.getBankAccount()).isEqualTo(bankAccount);
        assertThat(team.getProfileImageUrl()).isEqualTo(profileImageUrl);
        assertThat(team.getInviteCode()).hasSize(6);
    }

    @Test
    @DisplayName("팀 이름이 null이면 예외가 발생한다")
    void 팀_생성_실패_이름_null() {
        // given & when & then
        assertThatThrownBy(() -> Team.createTeam(null, "설명", TeamType.CLUB, 
                                               City.SEOUL, District.SEOUL_GANGNAM, "#FF0000", 
                                               true, GroupGender.MALE, 20, 
                                               "국민은행", "123-456-789", null))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀 이름은 필수입니다.");
    }

    @Test
    @DisplayName("팀 이름이 100자를 초과하면 예외가 발생한다")
    void 팀_생성_실패_이름_길이초과() {
        // given
        String longName = "a".repeat(101);

        // when & then
        assertThatThrownBy(() -> Team.createTeam(longName, "설명", TeamType.CLUB,
                                               City.SEOUL, District.SEOUL_GANGNAM, "#FF0000",
                                               true, GroupGender.MALE, 20,
                                               "국민은행", "123-456-789", null))
            .isInstanceOf(TeamControllerAdvice.class)
            .hasMessage("팀 이름은 100자 이내여야 합니다.");
    }

    @Test
    @DisplayName("멤버 제한 확인이 올바르게 동작한다")
    void 멤버_제한_확인() {
        // given
        Team team = Team.createTeam("Test Team", "설명", TeamType.CLUB,
                                   City.SEOUL, District.SEOUL_GANGNAM, "#FF0000",
                                   true, GroupGender.MALE, 5,
                                   "국민은행", "123-456-789", null);

        // when & then
        assertThat(team.canAddMember(4)).isTrue();  // 제한 미만
        assertThat(team.canAddMember(5)).isFalse(); // 제한 달성
        assertThat(team.canAddMember(6)).isFalse(); // 제한 초과
    }

    @Test
    @DisplayName("멤버 제한이 0이면 무제한으로 추가 가능하다")
    void 멤버_무제한_추가() {
        // given
        Team team = Team.createTeam("Test Team", "설명", TeamType.CLUB,
                                   City.SEOUL, District.SEOUL_GANGNAM, "#FF0000",
                                   true, GroupGender.MALE, 0,
                                   "국민은행", "123-456-789", null);

        // when & then
        assertThat(team.canAddMember(100)).isTrue();
        assertThat(team.canAddMember(1000)).isTrue();
    }
}