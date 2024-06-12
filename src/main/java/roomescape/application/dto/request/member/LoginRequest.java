package roomescape.application.dto.request.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "로그인 정보")
public record LoginRequest(
        @Schema(description = "이메일", example = "mang@woowa.net")
        @Size(max = 30)
        @Pattern(
                regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
                message = "이메일 또는 비밀번호를 형식에 맞춰 입력해주세요."
        ) String email,

        @Schema(description = "비밀번호", example = "password")
        @NotBlank(message = "이메일 또는 비밀번호를 형식에 맞춰 입력해주세요.")
        String password
) {
}
