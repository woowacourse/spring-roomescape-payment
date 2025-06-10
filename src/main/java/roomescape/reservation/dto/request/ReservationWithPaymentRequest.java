package roomescape.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;

public record ReservationWithPaymentRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        Long amount) {
    public Payment toPendingPayment() {
        return new Payment(orderId, paymentKey, amount, PaymentStatus.PENDING);
    }
}
