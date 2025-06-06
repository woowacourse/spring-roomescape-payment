package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank(message = "로그인 시 이메일 입력은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Schema(example = "member2@email.com")
        String email,

        @NotBlank(message = "로그인 시 비밀번호 입력은 필수입니다.")
        @Schema(example = "1234")
        String password) {
}
