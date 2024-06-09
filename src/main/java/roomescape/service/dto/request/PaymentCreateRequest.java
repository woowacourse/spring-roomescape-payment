package roomescape.service.dto.request;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

import java.math.BigDecimal;

public record PaymentCreateRequest(String paymentKey, String orderId, BigDecimal amount, Reservation reservation) {

    public Payment toPayment() {
        return Payment.tossPay(paymentKey, amount, reservation);
    }
}
