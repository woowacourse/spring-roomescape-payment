package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentErrorResponse(
        @Schema(description = "http status code") String code,
        @Schema(description = "에러 메시지") String message
) {
}
