package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Schema(description = "로그인 이메일", example = "mason@test.com")
        @NotBlank(message = "로그인 이메일은 필수입니다")
        @Email(message = "이메일 형태가 아닙니다")
        @Size(max = 320, message = "이메일의 길이를 다시 확인해주세요")
        String email,

        @Schema(description = "로그인 비밀번호", example = "123456")
        @NotBlank(message = "로그인 비밀번호는 필수입니다")
        @Size(min = 6, max = 20, message = "비밀번호의 길이를 다시 확인해주세요")
        String password
) {
}
