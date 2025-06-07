package roomescape.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "로그인 이메일은 비어 있을 수 없습니다.") String email,
        @NotBlank(message = "로그인 비밀번호는 비어 있을 수 없습니다.") String password
) {
}
