package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;
import roomescape.vo.Name;

@Schema(description = "테마 요청")
public record ThemeRequest(

        @Schema(description = "테마 이름", example = "홍길동전")
        String name,

        @Schema(description = "테마 설명", example = "조선시대로의 모험")
        String description,

        @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail,

        @Schema(description = "테마 가격", example = "30000")
        Long price) {

    public Theme toTheme() {
        return new Theme(new Name(name), description, thumbnail, price);
    }
}
