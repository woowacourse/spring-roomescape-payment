package roomescape.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.system.exception.ErrorType;

@Schema(name = "예외 응답", description = "예외 발생 시 응답에 사용됩니다.")
public record ErrorResponse(
        @Schema(description = "발생한 예외의 종류", example = "INVALID_REQUEST_DATA") ErrorType errorType,
        @Schema(description = "예외 메시지", example = "요청 데이터 값이 올바르지 않습니다.") String message
) {

    public static ErrorResponse of(ErrorType errorType, String message) {
        return new ErrorResponse(errorType, message);
    }
}
