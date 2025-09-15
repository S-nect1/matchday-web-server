package com.matchday.common.entity.enums;


import com.matchday.common.exception.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // 정상 code
    OK(HttpStatus.OK,"200", "OK"),

    // Common Error
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON000", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON001","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON002","권한이 잘못되었습니다."),
    _METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON003", "지원하지 않는 Http Method 입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON004", "금지된 요청입니다."),
    _INVALID_FORMAT(HttpStatus.BAD_REQUEST, "COMMON005", "날짜 형식이 잘못되었습니다."),
    _MISMATCHED_INPUT(HttpStatus.BAD_REQUEST, "COMMON006", "필드 타입이 일치하지 않습니다."),

    // Member Error
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER001", "올바르지 않은 사용자입니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER002", "닉네임은 필수 입니다."),

    // Review Error
    REVIEW_ALREADY_FOUND(HttpStatus.BAD_REQUEST, "REVIEW001", "지정된 리뷰의 개수를 초과했습니다."),

    // Match Error
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH001", "매치를 찾을 수 없습니다."),
    MATCH_REQUIRE_FEE(HttpStatus.BAD_REQUEST, "MATCH002", "대관비를 입력해주세요."),
    MATCH_REQUIRE_ACCOUNT(HttpStatus.BAD_REQUEST, "MATCH003", "계좌번호를 입력해주세요."),
    MATCH_ALREADY_FOUND(HttpStatus.BAD_REQUEST, "MATCH004", "이미 신청한 매치입니다."),
    MATCH_APPLICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "MATCH006", "올바른 매치가 아닙니다."),
    MATCH_TIME_OUT(HttpStatus.BAD_REQUEST, "MATCH007", "매치가 종료된 후 48시간이 지났습니다."),
    MATCH_NOT_CONFIRMED(HttpStatus.BAD_REQUEST, "MATCH008", "확정된 매치가 아닙니다."),
    MATCH_SCHEDULE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MATCH009", "매치 일정이 확정되지 않았거나, 존재하지 않습니다."),
    MATCH_DUPLICATED(HttpStatus.BAD_REQUEST, "MATCH010", "해당 시간에 다른 매치가 존재합니다."),
    MATCH_APPLICATION_INVALID_STATUS(HttpStatus.BAD_REQUEST, "MATCH011", "신청 상태에서만 처리할 수 있습니다."),
    MATCH_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "MATCH012", "이미 상대 팀이 배정된 매치입니다."),
    MATCH_SAME_TEAM(HttpStatus.BAD_REQUEST, "MATCH013", "같은 팀끼리는 매치할 수 없습니다."),
    MATCH_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "MATCH014", "상대 팀이 배정되지 않은 매치입니다."),
    MATCH_INVALID_SCORE(HttpStatus.BAD_REQUEST, "MATCH015", "점수는 0점 이상이어야 합니다."),
    MATCH_SCORE_REQUIRED(HttpStatus.BAD_REQUEST, "MATCH016", "매치 완료를 위해 점수를 입력해주세요."),
    MATCH_INVALID_DATE(HttpStatus.BAD_REQUEST, "MATCH017", "매치 날짜는 현재보다 미래여야 합니다."),
    MATCH_INVALID_TIME(HttpStatus.BAD_REQUEST, "MATCH018", "시작 시간은 종료 시간보다 빨라야 합니다."),
    MATCH_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "MATCH019", "이미 완료된 매치입니다."),

    // MatchUser Error


    // Statistic Error

    // Auth Success
    LOGIN_SUCCESS(HttpStatus.OK, "AUTH001", "로그인에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "AUTH002", "로그아웃에 성공했습니다."),
    TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "AUTH003", "토큰 갱신에 성공했습니다."),
    REGISTER_SUCCESS(HttpStatus.CREATED, "AUTH004", "회원가입에 성공했습니다."),
    
    // Auth Error
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH4001", "존재하지 않는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH4002", "비밀번호가 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH4003", "이미 존재하는 이메일입니다."),
    
    // Token Error
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN4001", "헤더에 토큰 값이 없습니다"),
    TOKEN_EXPIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "TOKEN4002", "토큰의 유효 기간이 만료되었습니다"),
    TOKEN_INVALID_EXCEPTION(HttpStatus.BAD_REQUEST, "TOKEN4003", "유효하지 않은 토큰입니다"),
    JWT_SIGNATURE_INVALID_EXCEPTION(HttpStatus.BAD_REQUEST, "TOKEN4004", "JWT 토큰이 올바르지 않습니다(header.payload.signature)"),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "TOKEN4005", "유효하지 않은 리프레시 토큰입니다."),

    // AWS S3 Error
    S3_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "S34001", "파일 업로드에 실패했습니다."),
    S3_PATH_NOT_FOUND(HttpStatus.BAD_REQUEST, "S34002", "파일이 존재하지 않습니다."),

    // Team Error
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM001", "팀을 찾을 수 없습니다."),
    TEAM_USER_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "TEAM002", "이미 팀에 가입되어 있습니다."),
    TEAM_LIMIT(HttpStatus.BAD_REQUEST, "TEAM003", "팀 인원이 가득 찼습니다."),
    BACK_NUMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "TEAM004", "이미 사용 중인 등번호입니다."),
    TEAM_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM005", "팀 멤버를 찾을 수 없습니다."),
    TEAM_LEADER_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "TEAM006", "팀장은 팀을 탈퇴할 수 없습니다."),
    TEAM_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "TEAM007", "팀 이름은 필수입니다."),
    TEAM_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "TEAM008", "팀 이름은 100자 이내여야 합니다."),
    TEAM_MEMBER_LIMIT_INVALID(HttpStatus.BAD_REQUEST, "TEAM009", "멤버 제한은 0 이상이어야 합니다."),
    TEAM_TYPE_REQUIRED(HttpStatus.BAD_REQUEST, "TEAM010", "팀 유형은 필수입니다."),
    TEAM_GENDER_REQUIRED(HttpStatus.BAD_REQUEST, "TEAM011", "팀 성별은 필수입니다."),
    BANK_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "TEAM012", "은행명은 필수입니다."),
    BANK_ACCOUNT_REQUIRED(HttpStatus.BAD_REQUEST, "TEAM013", "계좌번호는 필수입니다."),
    TEAM_ROLE_REQUIRED(HttpStatus.BAD_REQUEST, "TEAM014", "팀 역할은 필수입니다."),
    TEAM_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "TEAM015", "팀 정보는 필수입니다."),
    USER_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "TEAM016", "사용자 정보는 필수입니다."),
    // Schedule Error


    // File Error
    FILE_MAX_SIZE_OVER(HttpStatus.PAYLOAD_TOO_LARGE, "FILE4001", "100MB 이하 파일만 업로드 할 수 있습니다."),
    FILE_CONTENT_TYPE_NOT_IMAGE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FILE4002", "이미지 파일만 업로드할 수 있습니다."),
    FILE_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "FILE4003", "파일 저장에 실패했습니다. 서버에 문의하세요."),
    FILE_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "FILE4004", "지원하지 않는 파일 카테고리입니다."),

    // Enum Error
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "ENUM001", "지원하지 않는 카테고리입니다."),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, "ENUM002", "유효하지 않은 성별입니다."),
    INVALID_CLUB_CATEGORY(HttpStatus.BAD_REQUEST, "ENUM003", "유효하지 않은 클럽 종류입니다."),
    INVALID_AGE_RANGE(HttpStatus.BAD_REQUEST, "ENUM004", "유효하지 않은 연령대입니다."),
    INVALID_ACTIVITY_AREA(HttpStatus.BAD_REQUEST, "ENUM005", "유효하지 않은 지역입니다."),
    INVALID_MAIN_FOOT(HttpStatus.BAD_REQUEST, "ENUM006", "유효하지 않은 주발입니다."),
    INVALID_SPORTS_TYPE(HttpStatus.BAD_REQUEST, "ENUM007", "유효하지 않은 종목입니다."),
    INVALID_CLUB_ROLE(HttpStatus.BAD_REQUEST, "ENUM008", "유효하지 않은 모임 역할입니다."),
    INVALID_SCHEDULE_CATEGORY(HttpStatus.BAD_REQUEST, "ENUM009", "유효하지 않은 일정 종류입니다."),
    INVALID_ATTENDANCE_TYPE(HttpStatus.BAD_REQUEST, "ENUM010", "유효하지 않은 투표입니다.(참석/불참)");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
        // 결과 예시 - "Validation error - Reason why it isn't valid"
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getMessage());
    }

    public static ResponseCode valueOf(HttpStatus httpStatus) {
        if(httpStatus == null) {
            throw new GeneralException("HttpStatus is null.");
        }

        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
                .findFirst()
                .orElseGet(() -> {
                    if(httpStatus.is4xxClientError()) {
                        return ResponseCode._BAD_REQUEST;
                    } else if (httpStatus.is5xxServerError()) {
                        return ResponseCode._INTERNAL_SERVER_ERROR;
                    } else {
                        return ResponseCode.OK;
                    }
                });
    }
}
