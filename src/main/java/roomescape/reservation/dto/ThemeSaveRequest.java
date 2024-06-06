package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.reservation.domain.Description;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.ThemeName;

public record ThemeSaveRequest(
        @NotNull
        @NotBlank
        @Schema(description = "테마명", example = "우테코")
        String name,

        @NotNull
        @NotBlank
        @Schema(description = "테마 설명", example = "우테코를 수료하라")
        String description,

        @NotNull
        @NotBlank
        @Schema(description = "테마 이미지", example = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg")
        String thumbnail
) {

    public Theme toTheme() {
        return new Theme(new ThemeName(name), new Description(description), thumbnail);
    }
}
