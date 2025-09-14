package com.matchday.user.domain;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import com.matchday.modules.team.domain.enums.Position;
import com.matchday.modules.user.domain.User;
import com.matchday.modules.user.domain.enums.Gender;
import com.matchday.modules.user.domain.enums.UserRole;
import com.matchday.modules.user.repository.UserRepository;
import com.matchday.security.auth.auth.service.AuthService;
import com.matchday.security.utils.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserPasswordTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

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
        UserRole role = UserRole.ROLE_MEMBER;
        String phoneNumber = "010-1234-5678";
        City city = City.SEOUL;
        District district = District.SEOUL_GANGNAM;
        Boolean isProfessional = false;

        // when
        User user = User.createUser(email, passwordEncoder.encode(rawPassword), name, birth, height, gender,
                                  mainPosition, role, phoneNumber, city, district, 
                                  isProfessional);

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isNotEqualTo(rawPassword); // 암호화됨
        assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode(rawPassword));
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getBirth()).isEqualTo(birth);
    }

    @Test
    @DisplayName("올바른 비밀번호로 인증이 가능하다")
    void matchesPassword_WithCorrectPassword_ShouldReturnTrue() {
        // given
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = User.createUser("test@example.com", encodedPassword, "홍길동",
                                  LocalDate.of(1990, 1, 1), 180, Gender.MALE,
                                  Position.FW, UserRole.ROLE_MEMBER, "010-1234-5678",
                                  City.SEOUL, District.SEOUL_GANGNAM, false);
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        // when
        boolean matches = authService.matchesPassword(user, rawPassword);

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
                                  Position.FW, UserRole.ROLE_MEMBER, "010-1234-5678",
                                  City.SEOUL, District.SEOUL_GANGNAM, false);

        // when
        boolean matches = authService.matchesPassword(user, wrongPassword);

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
                                  Position.FW, UserRole.ROLE_MEMBER, "010-1234-5678",
                                  City.SEOUL, District.SEOUL_GANGNAM, false);
        String oldPasswordHash = user.getPassword();
        String encodedNew = passwordEncoder.encode(newPassword);
        String encodedOld = passwordEncoder.encode(oldPassword);

        given(passwordEncoder.matches("newpassword", encodedNew)).willReturn(true);
        given(passwordEncoder.matches("oldpassword", encodedOld)).willReturn(false);

        // when
        user.changePassword(encodedNew);

        // then
        assertThat(user.getPassword()).isNotEqualTo(oldPasswordHash); // 기존 해시와 다름
        assertThat(user.getPassword()).isNotEqualTo(newPassword); // 평문과 다름
        assertThat(authService.matchesPassword(user, newPassword)).isTrue(); // 새 비밀번호로 인증 가능
        assertThat(authService.matchesPassword(user, oldPasswordHash)).isFalse(); // 기존 비밀번호로 인증 불가
    }
}