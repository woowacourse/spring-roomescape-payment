package roomescape.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;

public record ReservationWithPaymentRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        Long amount) {
    public Payment toPendingPayment(Reservation reservation) {
        return new Payment(orderId, paymentKey, amount, PaymentStatus.PENDING, reservation);
    }
}
