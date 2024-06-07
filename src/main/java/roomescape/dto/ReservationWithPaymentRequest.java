package roomescape.dto;

import roomescape.domain.Payment;

import java.time.LocalDate;

public record ReservationWithPaymentRequest(LocalDate date,
                                            long timeId,
                                            long themeId,
                                            String paymentKey,
                                            String orderId,
                                            int amount) {

    public Payment toPayment() {
        return new Payment(paymentKey, orderId, amount);
    }
}
