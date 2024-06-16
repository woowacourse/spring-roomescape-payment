package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.theme.domain.Theme;
import roomescape.vo.Name;

@Tag(name = "테마 요청", description = "사용자 테마 정보 요청")
public record ThemeRequest(
        String name,
        String description,
        String thumbnail
) {
    public Theme toTheme() {
        return new Theme(new Name(name), description, thumbnail);
    }
}
