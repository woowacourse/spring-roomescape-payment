package roomescape.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.global.aop.Sensitive;

@Schema(description = "사용자 회원가입 시 요청 객체")
public record MemberRegisterRequest(
        @Schema(description = "사용자 이메일 주소", example = "newuser@example.com", requiredMode = REQUIRED)
        String email,

        @Schema(description = "사용자 비밀번호", example = "securePassword123", requiredMode = REQUIRED)
        @Sensitive
        String password,

        @Schema(description = "사용자 이름", example = "김철수", requiredMode = REQUIRED)
        String name
) {
}
