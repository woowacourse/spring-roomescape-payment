package roomescape.reservation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ThemeRequest(
        @NotBlank(message = "이름은 필수 값입니다.")
        String name,

        @NotBlank(message = "이름은 필수 값입니다.")
        String description,

        @NotBlank(message = "이름은 필수 값입니다.")
        String thumbnail,

        @NotNull(message = "금액은 필수 값입니다.")
        BigDecimal price
) {
}
