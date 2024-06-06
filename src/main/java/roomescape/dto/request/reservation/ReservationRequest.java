package roomescape.dto.request.reservation;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.dto.payment.PaymentRequest;

public record ReservationRequest(
        @NotNull LocalDate date,
        long timeId,
        long themeId,
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(orderId, amount, paymentKey);
    }
}
