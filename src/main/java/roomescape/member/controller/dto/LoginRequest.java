package roomescape.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;

@Schema(description = "회원 로그인 요청")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record LoginRequest(
        @Schema(description = "이메일", example = "siso@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(description = "비밀번호", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {

    public LoginRequest {
        validate(email, password);
    }

    private void validate(final String email, final String password) {
        Validator.of(LoginRequest.class)
                .notNullField(Fields.email, email)
                .notNullField(Fields.password, password);
    }
}
