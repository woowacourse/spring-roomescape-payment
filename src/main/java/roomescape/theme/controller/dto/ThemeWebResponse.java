package roomescape.theme.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "테마 정보 응답")
@FieldNameConstants
public record ThemeWebResponse(
        @Schema(description = "테마 ID", example = "1")
        Long id,
        @Schema(description = "테마 이름", example = "미스터리 하우스")
        String name,
        @Schema(description = "테마 설명", example = "미스터리 하우스는 고전적인 추리 테마입니다.")
        String description,
        @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail
) {

    public ThemeWebResponse {
        validate(id, name, description, thumbnail);
    }

    private static void validate(final Long id, final String name, final String description, final String thumbnail) {
        Validator.of(ThemeWebResponse.class)
                .notNullField(Fields.id, id)
                .notNullField(Fields.name, name)
                .notNullField(Fields.description, description)
                .notNullField(Fields.thumbnail, thumbnail);
    }
}
