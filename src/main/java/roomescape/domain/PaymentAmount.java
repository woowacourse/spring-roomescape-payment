package roomescape.domain;

import jakarta.persistence.Embeddable;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;

@Embeddable
public record PaymentAmount(Long amount) {
    private static final int MINIMUM_PAYMENT_AMOUNT = 0;

    public PaymentAmount {
        validateAmount(amount);
    }

    private void validateAmount(Long amount) {
        if (amount == null) {
            throw new RoomescapeException(ExceptionType.EMPTY_AMOUNT);
        }
        if (amount < MINIMUM_PAYMENT_AMOUNT) {
            throw new RoomescapeException(ExceptionType.INVALID_AMOUNT);
        }
    }
}
