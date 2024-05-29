package roomescape.service.response;

import roomescape.domain.Member;

public record MemberDto(Long id, String name, String role) {
    public static MemberDto from(Member member) {
        return new MemberDto(member.getId(), member.getName(), member.getRole().name());
    }
}
