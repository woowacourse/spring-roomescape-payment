package roomescape.domain.member.exception;

import roomescape.infrastructure.exception.DataNotFoundException;

public class MemberNotFoundException extends DataNotFoundException {
    public MemberNotFoundException(final String message) {
        super(message);
    }
}
