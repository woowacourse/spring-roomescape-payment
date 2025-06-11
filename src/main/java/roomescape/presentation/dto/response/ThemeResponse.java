package roomescape.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Theme;

import java.util.List;

@Schema(description = "테마 정보 응답 DTO")
public record ThemeResponse(
        @Schema(description = "테마 ID")
        Long id,

        @Schema(description = "테마 이름", example = "공포의 방탈출")
        String name,

        @Schema(description = "테마 설명", example = "무서운 공포 테마입니다.")
        String description,

        @Schema(description = "테마 썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail
) {

    public static List<ThemeResponse> from(List<Theme> themes) {
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
