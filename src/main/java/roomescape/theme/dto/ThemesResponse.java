package roomescape.theme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "테마 목록 조회 응답", description = "모든 테마 목록 조회 응답시 사용됩니다.")
public record ThemesResponse(
        @Schema(description = "모든 테마 목록") List<ThemeResponse> themes
) {
}
