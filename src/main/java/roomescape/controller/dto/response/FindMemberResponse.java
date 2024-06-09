package roomescape.controller.dto.response;

import roomescape.domain.member.Member;

public record FindMemberResponse(Long id, String name) {
    public static FindMemberResponse from(Member member) {
        return new FindMemberResponse(
                member.getId(),
                member.getName()
        );
    }
}
