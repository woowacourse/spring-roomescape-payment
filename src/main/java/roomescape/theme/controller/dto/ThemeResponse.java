package roomescape.theme.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import roomescape.theme.domain.Theme;

@Schema(description = "테마 응답 정보")
public record ThemeResponse(

        @Schema(description = "테마 ID", example = "1")
        Long id,

        @Schema(description = "테마 이름", example = "감옥에서 탈출하기")
        String name,

        @Schema(description = "테마 설명", example = "감옥에 갇힌 당신, 제한 시간 내에 탈출하라!")
        String description,

        @Schema(description = "테마 썸네일 URL", example = "https://example.com/image.jpg")
        String thumbnail

) {
    public static List<ThemeResponse> from(final List<Theme> themes) {
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public static ThemeResponse from(final Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName().name(),
                theme.getDescription().description(),
                theme.getThumbnail().thumbnail()
        );
    }
}
