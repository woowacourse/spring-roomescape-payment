package roomescape.global.exception;

import org.springframework.http.HttpStatus;

public record ErrorCode(HttpStatus status, String code, String message) {
}
