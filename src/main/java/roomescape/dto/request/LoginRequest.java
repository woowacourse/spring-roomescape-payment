package roomescape.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import roomescape.global.aop.Sensitive;

@Schema(description = "사용자 로그인 시 요청 객체")
public record LoginRequest(
        @Schema(description = "사용자 이메일 주소", example = "user@example.com", requiredMode = REQUIRED)
        @Email
        String email,

        @Schema(description = "사용자 비밀번호", example = "password123", requiredMode = REQUIRED)
        @Sensitive
        String password
) {
}
