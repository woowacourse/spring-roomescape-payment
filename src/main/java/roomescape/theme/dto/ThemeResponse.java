package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.theme.domain.Theme;

@Schema(name = "테마 정보", description = "테마 추가 및 조회 응답에 사용됩니다.")
public record ThemeResponse(
        @Schema(description = "테마 번호. 테마를 식별할 때 사용합니다.")
        Long id,
        @Schema(description = "테마 이름. 중복을 허용하지 않습니다.")
        String name,
        @Schema(description = "테마 설명")
        String description,
        @Schema(description = "테마 썸네일 이미지 URL")
        String thumbnail
) {

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail());
    }
}
