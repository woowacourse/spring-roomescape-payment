package roomescape.mvc.member.dto;

import roomescape.mvc.member.domain.Role;

public record MemberCreationContent(
        Role role,
        String name,
        String email,
        String password
) {

}
