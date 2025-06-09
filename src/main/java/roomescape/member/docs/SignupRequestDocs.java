package roomescape.member.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.dto.request.SignupRequest;

@Schema(description = "회원가입 요청")
public record SignupRequestDocs(
        @Schema(description = "이메일", example = "test@test.com")
        String email,
        @Schema(description = "이름", example = "홍길동")
        String name,
        @Schema(description = "비밀번호", example = "password123")
        String password) {

    public SignupRequest toSignupRequest() {
        return new SignupRequest(email, name, password);
    }
} 