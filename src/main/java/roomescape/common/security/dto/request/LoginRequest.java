package roomescape.common.security.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "로그인할 멤버의 이메일") String email,
        @Schema(description = "로그인할 멤버의 패스워드") String password) {
}
