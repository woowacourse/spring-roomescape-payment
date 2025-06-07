package roomescape.exception;

import roomescape.domain.member.Role;
import roomescape.exception.common.ForbiddenException;

public class AccessDeniedException extends ForbiddenException {

    public AccessDeniedException(Role currentAuthority, Role requiredAuthority, Long memberId) {
        super("기능에 대한 권한이 없습니다.",
                String.format("[사용자 권한이 잘못됐습니다] 현재 사용자 id : %d, 권한 : %s, 필요한 사용자 권한 : %s",
                        memberId, currentAuthority.name(), requiredAuthority.name()));
    }

    public AccessDeniedException(Long memberId) {
        super("기능에 대한 권한이 없습니다.",
                "[사용자 권한이 잘못됐습니다] 현재 사용자 id : " + memberId);
    }
}
