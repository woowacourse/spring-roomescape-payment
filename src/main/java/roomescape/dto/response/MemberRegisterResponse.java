package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.member.Member;

@Schema(description = "회원가입 성공 시 응답 객체")
public record MemberRegisterResponse(
        @Schema(description = "생성된 회원 ID", example = "1")
        long id,

        @Schema(description = "회원 이메일 주소", example = "test@example.com")
        String email,

        @Schema(description = "회원 이름", example = "김철수")
        String name
) {
    public static MemberRegisterResponse from(final Member member) {
        return new MemberRegisterResponse(member.getId(), member.getEmail(), member.getName());
    }
}
