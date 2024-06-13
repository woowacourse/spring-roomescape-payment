package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Token Request Model")
public record TokenRequest(@Schema(description = "Email address of the user", example = "example@example.com")
                           @Email String email,

                           @Schema(description = "User password", example = "password123")
                           @NotBlank String password) {
}
