package roomescape.member.domain;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.MemberExceptionCode;

@Tag(name = "멤버 역할", description = "사용자에게 권한을 부여하기 위한 역할이 정의되어 있다.")
public enum MemberRole {

    MEMBER,
    ADMIN;

    MemberRole() {
    }

    public static MemberRole findMemberRole(String role) {
        return Arrays.stream(MemberRole.values())
                .filter(memberRole -> memberRole.name().equals(role))
                .findAny()
                .orElseThrow(() -> new RoomEscapeException(MemberExceptionCode.MEMBER_ROLE_NOT_EXIST_EXCEPTION));
    }

    public boolean hasSameRoleFrom(MemberRole[] roles) {
        for (MemberRole memberRole : roles) {
            if (this.equals(memberRole)) {
                return true;
            }
        }
        return false;
    }
}
