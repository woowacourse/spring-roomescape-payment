package roomescape.domain.member.dto;

import roomescape.domain.member.domain.Role;

public record MemberCreationContent(
        Role role,
        String name,
        String email,
        String password
) {

}
