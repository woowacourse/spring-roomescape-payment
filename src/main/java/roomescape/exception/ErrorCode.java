package roomescape.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    LOGIN_NEEDED("로그인이 필요합니다."),
    ADMIN_ONLY("관리자만 접근할 수 있습니다."),
    MEMBER_NOT_FOUND("해당 회원을 찾을 수 없습니다."),
    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다."),
    MEMBER_ALREADY_EXISTS("이미 존재하는 회원입니다."),

    RESERVATION_MUST_BE_FUTURE("예약시간은 과거일 수 없습니다."),
    RESERVATION_NOT_FOUND("해당 예약을 찾을 수 없습니다."),
    RESERVATION_ALREADY_EXISTS("이미 예약된 시간입니다."),

    THEME_NOT_FOUND("해당 테마를 찾을 수 없습니다."),
    THEME_HAS_RESERVATION("해당 테마에 예약이 존재합니다."),
    THEME_ALREADY_EXISTS("이미 존재하는 테마입니다."),

    TIME_NOT_FOUND("해당 시간을 찾을 수 없습니다."),
    TIME_HAS_RESERVATION("해당 시간에 예약이 존재합니다."),
    TIME_ALREADY_EXISTS("이미 존재하는 예약 시간입니다."),

    WAITING_NOT_FOUND("해당 대기 예약을 찾을 수 없습니다."),
    WAITING_DUPLICATE_WITH_RESERVATION("예약과 중복된 시간으로 예약대기를 할 수 없습니다."),
    WAITING_ALREADY_EXISTS("이미 존재하는 대기 예약입니다."),
    ;

    private final String message;
}
