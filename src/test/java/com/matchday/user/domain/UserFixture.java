package com.matchday.user.domain;

import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.enums.Position;
import com.matchday.user.domain.enums.Gender;
import com.matchday.user.domain.enums.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

public class UserFixture {
    public static User defaultUser1() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();;

        return User.createUser("test@example.com", "password", "테스트유저",
                LocalDate.of(1990, 1, 1), 180, Gender.MALE,
                Position.MF, UserRole.ROLE_MEMBER, "010-1234-5678",
                City.SEOUL, District.SEOUL_GANGNAM, false, passwordEncoder);

    }

    public static User defaultUser2() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();;

        return User.createUser("test2@example.com", "password2", "테스트유저2",
                LocalDate.of(1999, 1, 1), 177, Gender.MALE,
                Position.MF, UserRole.ROLE_MEMBER, "010-4934-5678",
                City.SEOUL, District.SEOUL_DOBONG, true, passwordEncoder);

    }
}
