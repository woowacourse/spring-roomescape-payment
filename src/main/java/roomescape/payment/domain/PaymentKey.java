package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import roomescape.exception.BadArgumentRequestException;

@Embeddable
public record PaymentKey(
        @Column(length = 50, nullable = false, unique = true)
        String paymentKey) {

    private static final int MAX_LENGTH = 50;

    public PaymentKey {
        Objects.requireNonNull(paymentKey);
        if (paymentKey.isBlank() || paymentKey.length() > MAX_LENGTH) {
            throw new BadArgumentRequestException("결제 키는 1글자 이상 50글자 이하이어야 합니다.");
        }
    }
}
