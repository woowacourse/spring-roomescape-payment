package roomescape.theme.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "테마 생성 요청")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record CreateThemeWebRequest(
        @Schema(description = "테마 이름", example = "미스터리 하우스")
        String name,
        @Schema(description = "테마 설명", example = "미스터리 하우스는 고전적인 추리 테마입니다.")
        String description,
        @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail
) {

    public CreateThemeWebRequest {
        validate(name, description, thumbnail);
    }

    private void validate(final String name, final String description, final String thumbnail) {
        Validator.of(CreateThemeWebRequest.class)
                .notNullField(Fields.name, name)
                .notNullField(Fields.description, description)
                .notNullField(Fields.thumbnail, thumbnail);
    }
}
