package roomescape.application.dto.request.theme;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import roomescape.domain.reservationdetail.Theme;

@Schema(description = "테마 요청")
public record ThemeRequest(
        @Schema(description = "테마 이름", example = "세렌디피티: 뜻밖의 행운")
        @NotBlank(message = "이름은 빈값을 허용하지 않습니다.")
        String name,

        @Schema(description = "테마 설명", example = "행운을 불러오는 테마")
        @NotBlank(message = "설명은 빈값을 허용하지 않습니다.")
        String description,

        @Schema(description = "테마 썸네일", example = "https://roomescape.s3.ap-northeast-2.amazonaws.com/theme/thumbnail/serendipity.jpg")
        @NotBlank(message = "썸내일은 빈값을 허용하자 않습니다.")
        String thumbnail
) {

    public Theme toTheme() {
        return new Theme(name, description, thumbnail);
    }
}
