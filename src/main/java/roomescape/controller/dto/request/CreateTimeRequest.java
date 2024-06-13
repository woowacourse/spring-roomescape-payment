package roomescape.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateTimeRequest(
        @Schema(description = "시간", example = "08:00")
        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        @Pattern(regexp = "^(?:[01]\\d|2[0-3]):[0-5]\\d$", message = "HH:mm 형식으로 입력해 주세요.")
        String startAt
) {
}
