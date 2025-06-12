package roomescape.common.exception.error;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.Getter;
import roomescape.common.exception.CustomException;

@Schema(description = "오류 응답")
@Getter
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "400")
    private final int status;
    @Schema(description = "오류 코드", example = "INVALID_REQUEST")
    private final String code;
    @Schema(description = "오류 메시지", example = "잘못된 요청입니다.")
    private final String message;
    @Schema(description = "요청 경로", example = "POST /reservations")
    private final String path;
    @Schema(description = "오류 발생 시간", example = "2023-10-01T12:00:00")
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
