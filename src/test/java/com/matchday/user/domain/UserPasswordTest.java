package com.matchday.user.domain;

import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.enums.Position;
import com.matchday.user.domain.enums.Gender;
import com.matchday.user.domain.enums.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class UserPasswordTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("사용자 생성 시 비밀번호가 암호화되어 저장된다")
    void createUser_ShouldEncodePassword() {
        // given
        String email = "test@example.com";
        String rawPassword = "password123";
        String name = "홍길동";
        LocalDate birth = LocalDate.of(1990, 1, 1);
        Integer height = 180;
        Gender gender = Gender.MALE;
        Position mainPosition = Position.FW;
        MemberRole role = MemberRole.ROLE_MEMBER;
        String phoneNumber = "010-1234-5678";
        City city = City.SEOUL;
        District district = District.SEOUL_GANGNAM;
        Boolean isProfessional = false;

        // when
        User user = User.createUser(email, rawPassword, name, birth, height, gender, 
                                  mainPosition, role, phoneNumber, city, district, 
                                  isProfessional, passwordEncoder);

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isNotEqualTo(rawPassword); // 암호화됨
        assertThat(user.getPassword()).startsWith("$2a$"); // BCrypt 해시
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getBirth()).isEqualTo(birth);
    }

    @Test
    @DisplayName("올바른 비밀번호로 인증이 가능하다")
    void matchesPassword_WithCorrectPassword_ShouldReturnTrue() {
        // given
        String rawPassword = "password123";
        User user = User.createUser("test@example.com", rawPassword, "홍길동", 
                                  LocalDate.of(1990, 1, 1), 180, Gender.MALE,
                                  Position.FW, MemberRole.ROLE_MEMBER, "010-1234-5678",
                                  City.SEOUL, District.SEOUL_GANGNAM, false, passwordEncoder);

        // when
        boolean matches = user.matchesPassword(rawPassword, passwordEncoder);

        // then
        assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("잘못된 비밀번호로는 인증이 불가능하다")
    void matchesPassword_WithWrongPassword_ShouldReturnFalse() {
        // given
        String rawPassword = "password123";
        String wrongPassword = "wrongpassword";
        User user = User.createUser("test@example.com", rawPassword, "홍길동", 
                                  LocalDate.of(1990, 1, 1), 180, Gender.MALE,
                                  Position.FW, MemberRole.ROLE_MEMBER, "010-1234-5678",
                                  City.SEOUL, District.SEOUL_GANGNAM, false, passwordEncoder);

        // when
        boolean matches = user.matchesPassword(wrongPassword, passwordEncoder);

        // then
        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("비밀번호 변경 시 새로운 비밀번호로 암호화된다")
    void changePassword_ShouldEncodeNewPassword() {
        // given
        String oldPassword = "oldpassword";
        String newPassword = "newpassword";
        User user = User.createUser("test@example.com", oldPassword, "홍길동", 
                                  LocalDate.of(1990, 1, 1), 180, Gender.MALE,
                                  Position.FW, MemberRole.ROLE_MEMBER, "010-1234-5678",
                                  City.SEOUL, District.SEOUL_GANGNAM, false, passwordEncoder);
        String oldPasswordHash = user.getPassword();

        // when
        user.changePassword(newPassword, passwordEncoder);

        // then
        assertThat(user.getPassword()).isNotEqualTo(oldPasswordHash); // 기존 해시와 다름
        assertThat(user.getPassword()).isNotEqualTo(newPassword); // 평문과 다름
        assertThat(user.matchesPassword(newPassword, passwordEncoder)).isTrue(); // 새 비밀번호로 인증 가능
        assertThat(user.matchesPassword(oldPassword, passwordEncoder)).isFalse(); // 기존 비밀번호로 인증 불가
    }
}