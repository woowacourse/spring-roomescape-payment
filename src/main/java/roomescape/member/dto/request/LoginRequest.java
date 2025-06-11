package roomescape.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(

        @Schema(description = "이메일", example = "admin@email.com")
        @NotNull String email,

        @Schema(description = "비밀번호", example = "1234")
        @NotNull String password
) {
}
