package com.matchday.auth.dto;

import com.matchday.global.entity.enums.City;
import com.matchday.global.entity.enums.District;
import com.matchday.team.domain.enums.Position;
import com.matchday.user.domain.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자를 초과할 수 없습니다")
    private String name;
    
    @NotNull(message = "생년월일은 필수입니다")
    @Past(message = "생년월일은 과거 날짜여야 합니다")
    private LocalDate birth;
    
    @NotNull(message = "키는 필수입니다")
    @Min(value = 100, message = "키는 100cm 이상이어야 합니다")
    @Max(value = 250, message = "키는 250cm 이하여야 합니다")
    private Integer height;
    
    @Min(value = 30, message = "몸무게는 30kg 이상이어야 합니다")
    @Max(value = 200, message = "몸무게는 200kg 이하여야 합니다")
    private Integer weight;
    
    @NotNull(message = "성별은 필수입니다")
    private Gender gender;
    
    @NotNull(message = "주 포지션은 필수입니다")
    private Position mainPosition;
    
    private Position subPosition;
    
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", message = "전화번호 형식이 올바르지 않습니다 (예: 010-1234-5678)")
    private String phoneNumber;
    
    @NotNull(message = "시/도는 필수입니다")
    private City city;
    
    @NotNull(message = "구/군은 필수입니다")
    private District district;
    
    @NotNull(message = "프로 여부는 필수입니다")
    private Boolean isProfessional;
    
    private String description;
}