package roomescape.theme.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import roomescape.theme.domain.Theme;

@Schema(name = "ThemeRequest(테마 생성 요청 DTO)")
public record ThemeRequest(
        @NotBlank String name,
        @NotBlank String description,
        @Size(max = 255) @NotBlank String thumbnail
) {

    public Theme toEntity() {
        return new Theme(name, description, thumbnail);
    }
}
