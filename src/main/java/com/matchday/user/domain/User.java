package com.matchday.user.domain;

import com.matchday.global.entity.BaseEntity;
import com.matchday.global.entity.enums.*;
import com.matchday.team.domain.enums.Position;
import com.matchday.user.domain.enums.FootType;
import com.matchday.user.domain.enums.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;


@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate birth;
    @Column(nullable = false)
    private Integer height;
    private Integer weight;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position mainPosition;
    @Enumerated(EnumType.STRING)
    private Position subPosition;

    @Enumerated(EnumType.STRING)
    private FootType mainFoot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    private String phoneNumber;

    private String description;

    @Enumerated(EnumType.STRING)
    private City city;
    @Enumerated(EnumType.STRING)
    private District district;

    @Column(nullable = false)
    private Boolean isProfessional;

    public static User createUser(String email, String rawPassword, String name, 
                                 LocalDate birth, Integer height, Gender gender, 
                                 Position mainPosition, MemberRole role, String phoneNumber,
                                 City city, District district, Boolean isProfessional,
                                 PasswordEncoder passwordEncoder) {
        User user = new User();
        user.email = email;
        user.password = passwordEncoder.encode(rawPassword);
        user.name = name;
        user.birth = birth;
        user.height = height;
        user.gender = gender;
        user.mainPosition = mainPosition;
        user.role = role;
        user.phoneNumber = phoneNumber;
        user.city = city;
        user.district = district;
        user.isProfessional = isProfessional;
        return user;
    }

    public void changePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(rawPassword);
    }

    public boolean matchesPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
}