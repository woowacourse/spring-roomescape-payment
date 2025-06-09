package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;

@Schema(description = "회원 응답 객체")
public record MemberResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "회원 이름", example = "John Doe")
        String name,

        @Schema(description = "회원 이메일", example = "john.doe@example.com")
        String email
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }
}
