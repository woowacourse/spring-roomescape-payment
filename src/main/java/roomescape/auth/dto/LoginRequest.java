package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull
        @NotBlank
        @Schema(description = "로그인 이메일", example = "kaki@email.com")
        String email,

        @NotNull
        @NotBlank
        @Schema(description = "로그인 비밀번호", example = "1234")
        String password
) {
}
