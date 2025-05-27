package roomescape.global.dto;

import roomescape.member.domain.MemberName;
import roomescape.member.domain.MemberRole;

public record SessionMember(Long id, MemberName name, MemberRole role) {
}
