package roomescape.system.dto.response;

import roomescape.system.exception.ErrorType;

public record ErrorResponse(ErrorType errorType, String message) {

    public static ErrorResponse of(ErrorType errorType, String message) {
        return new ErrorResponse(errorType, message);
    }
}
