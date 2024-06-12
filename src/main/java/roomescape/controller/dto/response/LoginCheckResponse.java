package roomescape.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.member.Role;

public record LoginCheckResponse(
        @Schema(description = "회원명", example = "켬미")
        String name,
        @Schema(description = "역할", example = "USER")
        Role role
) {
}
