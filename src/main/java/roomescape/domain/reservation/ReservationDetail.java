package roomescape.domain.reservation;

import org.springframework.lang.Nullable;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentKey;

public record ReservationDetail(
    ReservationWithOrder waitingInfo,
    @Nullable PaymentKey paymentKey,
    @Nullable Long amount
) {

    public static ReservationDetail ofWaiting(ReservationWithOrder waitingInfo) {
        return new ReservationDetail(waitingInfo, null, null);
    }

    public static ReservationDetail ofReserved(ReservationWithOrder waitingInfo, Payment payment) {
        return new ReservationDetail(waitingInfo, payment.paymentKey(), payment.totalAmount());
    }
}
