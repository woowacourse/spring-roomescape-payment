package roomescape.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.experimental.FieldNameConstants;
import roomescape.common.utils.Validator;
import roomescape.member.auth.vo.MemberInfo;

@Schema(description = "회원 정보 요청")
@FieldNameConstants(level = AccessLevel.PRIVATE)
public record MemberInfoResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,
        @Schema(description = "이름", example = "시소")
        String name,
        @Schema(description = "이메일", example = "siso@gmail.com")
        String email
) {

    public MemberInfoResponse {
        validate(id, name, email);
    }

    private void validate(final Long id,
                          final String name,
                          final String email) {
        Validator.of(MemberInfo.class)
                .notNullField(Fields.id, id)
                .notNullField(Fields.name, name)
                .notNullField(Fields.email, email);
    }
}
