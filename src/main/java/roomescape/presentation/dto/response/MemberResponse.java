package roomescape.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;
import roomescape.presentation.dto.request.LoginMember;

import java.util.List;

public record MemberResponse(
        @Schema(example = "3")
        Long id,

        @Schema(example = "회원2")
        String name,

        @Schema(example = "member2@email.com")
        String email
) {

    public static MemberResponse from(LoginMember loginMember) {
        return new MemberResponse(loginMember.id(), loginMember.name(), loginMember.email());
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public static List<MemberResponse> from(List<Member> members) {
        return members.stream()
                .map(MemberResponse::from)
                .toList();
    }
}
