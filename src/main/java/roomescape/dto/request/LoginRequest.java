package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "user1@email.com")
        @NotBlank @Email
        String email,

        @Schema(example = "1234")
        @NotBlank
        String password
) {
}
