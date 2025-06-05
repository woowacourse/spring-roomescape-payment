package roomescape.domain.waiting.exception;

import roomescape.infrastructure.exception.DataNotFoundException;

public class WaitingNotFoundException extends DataNotFoundException {
    public WaitingNotFoundException(final String message) {
        super(message);
    }
}
