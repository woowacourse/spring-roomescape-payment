package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(example = "돔푸")
        String name
) {
}
