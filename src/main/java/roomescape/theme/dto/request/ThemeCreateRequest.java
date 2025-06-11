package roomescape.theme.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import roomescape.theme.entity.Theme;

public record ThemeCreateRequest(

        @Schema(description = "이름", example = "테마15")
        @NotNull String name,

        @Schema(description = "설명", example = "테마의 설명입니다.")
        @NotNull String description,

        @Schema(description = "썸네일", example = "http://~")
        @NotNull String thumbnail
) {
    public Theme toEntity() {
        return new Theme(name, description, thumbnail);
    }
}
