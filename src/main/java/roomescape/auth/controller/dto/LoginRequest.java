package roomescape.auth.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "이메일 형식에 맞지 않습니다.") @NotBlank(message = "이메일을 입력하지 않았습니다.") String email,
        @NotBlank(message = "비밀번호를 입력하지 않았습니다.") String password
) {
}
