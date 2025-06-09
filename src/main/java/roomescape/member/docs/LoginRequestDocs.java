package roomescape.member.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.dto.request.LoginRequest;

@Schema(description = "로그인 요청")
public record LoginRequestDocs(
        @Schema(description = "이메일", example = "test@test.com")
        String email,
        @Schema(description = "비밀번호", example = "password123")
        String password) {

    public LoginRequest toLoginRequest() {
        return new LoginRequest(email, password);
    }
} 