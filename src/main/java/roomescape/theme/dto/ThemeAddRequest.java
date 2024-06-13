package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import roomescape.theme.domain.Theme;

public record ThemeAddRequest(

        @Schema(description = "테마 이름", example = "테마명")
        @NotBlank(message = "테마 이름은 필수입니다.")
        @Size(max = 40, message = "테마 이름의 길이를 확인해주세요")
        String name,

        @Schema(description = "테마 설명", example = "테마 설명입니다.")
        @NotBlank(message = "테마 설명은 필수입니다.")
        @Size(max = 80, message = "테마 설명의 길이를 확인해주세요")
        String description,

        @Schema(description = "테마 썸네일 경로", example = "테마 썸네일 경로")
        @NotBlank(message = "테마 썸네일 경로는 필수입니다.")
        String thumbnail
) {

    public Theme toTheme() {
        return new Theme(null, name, description, thumbnail);
    }
}
