package roomescape.presentation.dto.response;

import roomescape.business.model.entity.User;

public record UserResponse(
        String id,
        String name,
        String email
) {
    public static UserResponse from(final User user) {
        return new UserResponse(user.getId().value(), user.getName().value(), user.getEmail().value());
    }
}
