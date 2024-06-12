package roomescape.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;


public record ErrorMessageResponse(@Schema(description = "에러 메시지", example = "문제가 발생했습니다.") String message) {
}
