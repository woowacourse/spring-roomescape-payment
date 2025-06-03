package roomescape.presentation.dto.response;

import roomescape.business.model.entity.Member;

public record UserResponse(
        String id,
        String name,
        String email
) {
    public static UserResponse from(final Member member) {
        return new UserResponse(member.getId().value(), member.getName().value(), member.getEmail().value());
    }
}
