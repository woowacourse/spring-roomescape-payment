package roomescape.member.domain;

import roomescape.common.exception.BadRequestException;

public enum Role {
    ADMIN,
    MEMBER;

    public static Role from(final String value) {
        for (Role role : values()) {
            if (role.name().equals(value)) {
                return role;
            }
        }
        throw new BadRequestException("존재하지 않는 권한입니다.");
    }
}
