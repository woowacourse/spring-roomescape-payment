package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "에러 메시지") String message
) {
}
