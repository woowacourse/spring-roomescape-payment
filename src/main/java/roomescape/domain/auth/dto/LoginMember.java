package roomescape.domain.auth.dto;

import roomescape.domain.member.MemberRole;

public record LoginMember(String name, String email, MemberRole role) {
}
