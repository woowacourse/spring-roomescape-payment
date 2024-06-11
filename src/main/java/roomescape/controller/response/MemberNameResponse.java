package roomescape.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberNameResponse(
        @Schema(description = "멤버 이름", example = "수달")
        String name) {
}
