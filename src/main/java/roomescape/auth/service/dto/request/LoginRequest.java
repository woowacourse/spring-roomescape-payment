package roomescape.auth.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest(로그인 요청 DTO)")
public record LoginRequest(

        @NotBlank String email,
        @NotBlank String password
) {
}
