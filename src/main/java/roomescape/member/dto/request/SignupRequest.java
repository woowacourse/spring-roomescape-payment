package roomescape.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청")
public record SignupRequest(
        @Schema(description = "이메일", example = "test@test.com")
        String email,
        @Schema(description = "이름", example = "홍길동")
        String name,
        @Schema(description = "비밀번호", example = "password123")
        String password) {

    private static final String EMAIL_SIGN = "@";

    public SignupRequest {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어있을 수 없습니다.");
        }
        if (!email.contains(EMAIL_SIGN)) {
            throw new IllegalArgumentException("이메일의 형식에 맞게 작성해주세요.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        }
    }
}
