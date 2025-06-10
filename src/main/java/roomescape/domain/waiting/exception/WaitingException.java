package roomescape.domain.waiting.exception;

import roomescape.infrastructure.exception.DomainException;

public class WaitingException extends DomainException {
    public WaitingException(final String message) {
        super(message);
    }
}
