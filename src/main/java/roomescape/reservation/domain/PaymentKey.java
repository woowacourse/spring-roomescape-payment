package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record PaymentKey(
        @Column(name = "payment_key", nullable = false)
        String paymentKey
) {

}
