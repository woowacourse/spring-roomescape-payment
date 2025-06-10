package roomescape.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "회원 이메일", example = "wooga@gmail.com")
        String email,

        @Schema(description = "회원 비밀번호", example = "password123")
        String password
) {
}
