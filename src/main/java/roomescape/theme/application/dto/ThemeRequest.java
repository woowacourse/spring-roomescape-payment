package roomescape.theme.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ThemeRequest(
        @NotBlank String name,
        String description,
        String thumbnail,
        BigDecimal price
) {
}
