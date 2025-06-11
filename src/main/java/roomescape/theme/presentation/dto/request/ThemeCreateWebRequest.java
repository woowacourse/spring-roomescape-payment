package roomescape.theme.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;

public record ThemeCreateWebRequest(
        @Schema(description = "생성할 테마의 이름") String name,
        @Schema(description = "생성할 테마의 설명") String description,
        @Schema(description = "생성할 테마의 썸네일 Url") String thumbnail
) {
    public ThemeCreateWebRequest {
        if (name == null) {
            throw new IllegalArgumentException("이름은 null일 수 없습니다.");
        }
        if (description == null) {
            throw new IllegalArgumentException("설명은 null일 수 없습니다.");
        }
        if (thumbnail == null) {
            throw new IllegalArgumentException("썸네일은 null일 수 없습니다.");
        }
    }

    public Theme toTheme() {
        return new Theme(name, description, thumbnail);
    }
}
