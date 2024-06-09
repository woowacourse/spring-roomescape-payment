package roomescape.system.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "로그인 요청", description = "로그인 요청 시 사용됩니다.")
public record LoginRequest(
        @NotBlank(message = "이메일은 null 또는 공백일 수 없습니다.")
        @Email(message = "이메일 형식이 일치하지 않습니다. 예시: abc123@gmail.com)")
        @Schema(description = "필수 값이며, 이메일 형식으로 입력해야 합니다.", example = "abc123@gmail.com")
        String email,
        @NotBlank(message = "비밀번호는 null 또는 공백일 수 없습니다.")
        @Schema(description = "최소 1글자 이상 입력해야 합니다.")
        String password
) {
}
