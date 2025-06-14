package roomescape.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MemberRole {

    USER("USER"),
    ADMIN("ADMIN"),
    NONE("NONE");

    private final String name;
}
