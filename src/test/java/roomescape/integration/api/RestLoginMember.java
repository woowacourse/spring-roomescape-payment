package roomescape.integration.api;


import roomescape.member.domain.Member;

public record RestLoginMember(Member member, String sessionId) {
}
