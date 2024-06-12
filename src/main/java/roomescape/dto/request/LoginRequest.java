package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "로그인 요청 이메일") String email,
        @Schema(description = "로그인 요청 비밀번호") String password
) {
}
