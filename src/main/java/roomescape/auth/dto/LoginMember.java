package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.auth.domain.Role;

public record LoginMember(
        @Schema(description = "회원 ID", example = "1")
        Long id,

        Role role,

        @Schema(description = "로그인 이름", example = "카키")
        String name,

        @Schema(description = "로그인 이메일", example = "kaki@email.com")
        String email
) {
}
