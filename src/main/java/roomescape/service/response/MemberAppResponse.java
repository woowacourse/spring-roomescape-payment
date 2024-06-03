package roomescape.service.response;

import roomescape.domain.Member;

public record MemberAppResponse(Long id, String name, String role) {
    public static MemberAppResponse from(Member member) {
        return new MemberAppResponse(member.getId(), member.getName(), member.getRole().name());
    }
}
