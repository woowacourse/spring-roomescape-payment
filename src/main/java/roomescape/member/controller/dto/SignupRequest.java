package roomescape.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "회원가입 요청")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record SignupRequest(
        @Schema(description = "이메일", example = "siso@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "비밀번호", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,
        @Schema(description = "이름", example = "시소", requiredMode = Schema.RequiredMode.REQUIRED)
        String name
) {

    public SignupRequest {
        validate(email, password, name);
    }

    private void validate(final String email, final String password, final String name) {
        Validator.of(SignupRequest.class)
                .notNullField(Fields.email, email)
                .notNullField(Fields.password, password)
                .notBlankField(Fields.name, name);
    }
}
