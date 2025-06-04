package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.member.Member;

public record MemberRegisterResponse(
        @Schema(example = "1")
        long id,

        @Schema(example = "dompoo@gmail.com")
        String email,
        
        @Schema(example = "돔푸")
        String name
) {
    public static MemberRegisterResponse from(final Member member) {
        return new MemberRegisterResponse(member.getId(), member.getEmail(), member.getName());
    }
}
