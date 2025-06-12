package roomescape.member.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.Member;

@Schema(name = "MemberResponse(멤버 조회 응답 DTO)")
public record MemberResponse(
        Long id,
        String name,
        String email
) {

    public static MemberResponse fromEntity(final Member member) {
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }
}
