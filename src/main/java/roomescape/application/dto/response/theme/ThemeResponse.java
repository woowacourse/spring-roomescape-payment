package roomescape.application.dto.response.theme;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.reservationdetail.Theme;

@Schema(name = "테마 정보")
public record ThemeResponse(
        @Schema(description = "테마 ID", example = "1")
        Long id,

        @Schema(description = "테마 이름", example = "세렌디피티: 뜻밖의 행운")
        String name,

        @Schema(description = "테마 설명", example = "행운을 불러오는 테마")
        String description,

        @Schema(description = "테마 썸네일", example = "https://roomescape.s3.ap-northeast-2.amazonaws.com/theme/thumbnail/serendipity.jpg")
        String thumbnail
) {

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
    }
}
