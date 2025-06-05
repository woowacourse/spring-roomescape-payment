package roomescape.infrastructure.exception;

public class DomainRuleException extends RuntimeException {
    public DomainRuleException(final String message) {
        super(message);
    }
}
