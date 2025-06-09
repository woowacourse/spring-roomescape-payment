package roomescape.member.domain;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import roomescape.global.exception.AccessDeniedException;

@Slf4j
public enum MemberRole {
    ADMIN(List.of("ADMIN")),
    MEMBER(List.of("MEMBER")),
    ;

    List<String> types;

    MemberRole(final List<String> types) {
        this.types = types;
    }

    public static MemberRole from(final String roleName) {
        return Arrays.stream(values())
                .filter(role -> role.types.contains(roleName))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("[ROLE-MAP] 존재하지 않는 권한 요청: {}", roleName);
                    return new AccessDeniedException("존재하지 않는 권한입니다.");
                });
    }

    public String getPrimaryType() {
        return name();
    }
}
