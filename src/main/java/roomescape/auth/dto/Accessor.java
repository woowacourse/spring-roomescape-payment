package roomescape.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record Accessor(
        @Schema(description = "로그인한 회원 id", example = "1")
        @NotNull
        @Positive
        Long id) {

}
