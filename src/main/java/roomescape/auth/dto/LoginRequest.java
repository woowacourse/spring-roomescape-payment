package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 로그인 요청")
public record LoginRequest(
        @Schema(description = "사용자 이메일", example = "admin@email.com") String email,
        @Schema(description = "사용자 비밀번호", example = "password") String password) {
}
