package com.matchday.modules.user.domain;

import com.matchday.common.entity.BaseEntity;
import com.matchday.common.entity.enums.*;
import com.matchday.modules.team.domain.enums.Position;
import com.matchday.modules.user.domain.enums.FootType;
import com.matchday.modules.user.domain.enums.Gender;
import com.matchday.modules.user.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
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
    private UserRole role;

    private String phoneNumber;

    private String description;

    @Enumerated(EnumType.STRING)
    private City city;
    @Enumerated(EnumType.STRING)
    private District district;

    @Column(nullable = false)
    private Boolean isProfessional;

    private Integer backNumber;

    public static User createUser(String email, String encodedPassword, String name,
                                  LocalDate birth, Integer height, Gender gender,
                                  Position mainPosition, UserRole role, String phoneNumber,
                                  City city, District district, Boolean isProfessional) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
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

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
    
    public void updateWeight(Integer weight) {
        this.weight = weight;
    }
    
    public void updateSubPosition(Position subPosition) {
        this.subPosition = subPosition;
    }
    
    public void updateDescription(String description) {
        this.description = description;
    }
}