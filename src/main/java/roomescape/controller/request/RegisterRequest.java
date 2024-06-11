package roomescape.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotNull
        @NotEmpty
        @Schema(description = "멤버 이름", example = "수달")
        String name,
        @NotNull
        @NotEmpty
        @Schema(description = "이메일", example = "otter@email.com")
        String email,
        @NotNull
        @NotEmpty
        @Schema(description = "비밀번호", example = "1234")
        String password) {
}
