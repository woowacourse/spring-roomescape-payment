package roomescape.member.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.Member;

@Schema(description = "회원 응답 정보")
public record MemberResponse(
        @Schema(description = "회원 ID")
        Long id,

        @Schema(description = "회원 이름")
        String name,

        @Schema(description = "회원 이메일")
        String email
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName().getValue(), member.getEmail().getValue());
    }
}
