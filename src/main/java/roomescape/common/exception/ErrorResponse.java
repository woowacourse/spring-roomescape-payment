package roomescape.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int status;
    private final String code;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;

    public ErrorResponse(int status, String code, String message, String method, String uri) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = method + " " + uri;
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse of(CustomException e, HttpServletRequest request) {
        return new ErrorResponse(e.getErrorCode().getStatusValue(), e.getErrorCode().name(), e.getMessageForClient(), request.getMethod(), request.getRequestURI());
    }

    public static ErrorResponse of(ErrorCode errorCode, HttpServletRequest request) {
        return new ErrorResponse(errorCode.getStatusValue(), errorCode.name(), errorCode.getMessage(), request.getMethod(), request.getRequestURI());
    }
}
