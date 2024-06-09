package roomescape.controller.dto.response;

import roomescape.domain.member.Role;

public record LoginCheckResponse(String name, Role role) {
}
