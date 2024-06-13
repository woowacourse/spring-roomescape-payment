package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예외를 전달하는 DTO")
public record ErrorResponse(@Schema(description = "예외 메시지", example = "예기치 못한 오류입니다.") String message) {
}
