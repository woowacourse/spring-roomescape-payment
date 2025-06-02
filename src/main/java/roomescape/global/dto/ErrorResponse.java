package roomescape.global.dto;

import roomescape.global.exception.BusinessException;

public record ErrorResponse(String message) {

    public static ErrorResponse from(final BusinessException exception) {
        return new ErrorResponse(exception.getMessage());
    }
}
