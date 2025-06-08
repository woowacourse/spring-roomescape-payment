package roomescape.domain.reservation;

import org.springframework.lang.Nullable;
import roomescape.domain.payment.PaymentKey;

public record ReservationDetail(
    ReservationWithOrder waiting,
    @Nullable PaymentKey paymentKey,
    @Nullable Long amount
) {

}
