package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "사용자 이메일", example = "user@example.com")
        @NotBlank(message = "로그인 시 이메일 입력은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @Schema(description = "사용자 비밀번호", example = "password123")
        @NotBlank(message = "로그인 시 비밀번호 입력은 필수입니다.")
        String password) {
}
