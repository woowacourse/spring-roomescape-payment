package roomescape.auth.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @NotBlank(message = "이름은 필수 값입니다.")
        String name,

        @Email(message = "이메일 형식에 맞지 않습니다.")
        @NotBlank(message = "이메일은 필수 값입니다.")
        String email,

        @NotBlank(message = "패드워드는 필수 값입니다.")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
                message = "패스워드는 최소 8자 이상, 하나 이상의 대문자, 소문자, 숫자, 특수 문자를 포함해야 합니다."
        )
        String password
) {
}
