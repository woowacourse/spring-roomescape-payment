package roomescape.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "로그인 확인 요청")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record LoginCheckResponse(
        @Schema(description = "이름", example = "시소")
        String name
) {

    public LoginCheckResponse {
        validate(name);
    }

    private void validate(final String name) {
        Validator.of(LoginCheckResponse.class)
                .notNullField(Fields.name, name);
    }
}
