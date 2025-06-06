package roomescape.mvc.member.response;

import roomescape.mvc.member.domain.Member;

public record AddMemberResponse(
        Long id,
        String roleName,
        String name
) {

    public AddMemberResponse(Member member) {
        this(member.getId(), member.getRole().toString(), member.getName());
    }
}
