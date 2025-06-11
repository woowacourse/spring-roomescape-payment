package roomescape.common.exception.handler;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(@Schema(description = "에러 메세지") String message) {
}
