package roomescape.exception.dto;

import org.springframework.http.HttpStatus;

public record PaymentErrorResponse(
        HttpStatus httpStatus,
        String message
) {
}
