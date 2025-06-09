package roomescape.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청")
public record LoginRequest(
        @Schema(description = "이메일", example = "test@test.com")
        String email,
        @Schema(description = "비밀번호", example = "password123")
        String password) {
    public LoginRequest {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어있을 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        }
    }
}
