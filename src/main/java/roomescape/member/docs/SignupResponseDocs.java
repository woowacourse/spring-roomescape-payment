package roomescape.member.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.dto.response.SignupResponse;

@Schema(description = "회원가입 응답")
public record SignupResponseDocs(
        @Schema(description = "회원 ID", example = "1")
        Long id,
        @Schema(description = "회원 이름", example = "홍길동")
        String name,
        @Schema(description = "이메일", example = "test@test.com")
        String email,
        @Schema(description = "비밀번호", example = "password123")
        String password) {

    public static SignupResponseDocs from(SignupResponse response) {
        return new SignupResponseDocs(response.id(), response.name(), response.email(), response.password());
    }
} 