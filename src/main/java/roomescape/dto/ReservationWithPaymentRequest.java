package roomescape.dto;

import roomescape.domain.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationWithPaymentRequest(LocalDate date,
                                            long timeId,
                                            long themeId,
                                            String paymentKey,
                                            String orderId,
                                            BigDecimal amount) {

    public Payment toPayment() {
        return new Payment(paymentKey, orderId, amount);
    }
}
