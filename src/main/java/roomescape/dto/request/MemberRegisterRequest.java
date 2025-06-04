package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberRegisterRequest(
        @Schema(example = "dompoo@gmail.com")
        @NotBlank @Email
        String email,

        @Schema(example = "password1234")
        @NotBlank
        String password,

        @Schema(example = "돔푸")
        @NotBlank
        String name
) {
}
