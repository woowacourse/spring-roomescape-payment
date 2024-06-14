package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "사용자 이름", example = "낙낙")
        String name) {
}
