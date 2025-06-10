package roomescape.domain.member.exception;

import roomescape.infrastructure.exception.DomainException;

public class MemberException extends DomainException {
    public MemberException(final String message) {
        super(message);
    }
}
