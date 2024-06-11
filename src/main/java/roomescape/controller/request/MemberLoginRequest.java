package roomescape.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MemberLoginRequest(
        @NotNull
        @NotEmpty
        @Schema(description = "비밀번호", example = "1234")
        String password,
        @NotNull
        @NotEmpty
        @Schema(description = "이메일", example = "otter@email.com")
        String email) {
}
