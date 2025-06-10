package roomescape.domain.waiting.exception;

import roomescape.infrastructure.exception.DomainRuleException;

public class WaitingNotAllowedException extends DomainRuleException {
    public WaitingNotAllowedException(final String message) {
        super(message);
    }
}
