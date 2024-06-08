package roomescape.service.dto.request;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

import java.math.BigDecimal;

public record PaymentRequest(String paymentKey, String orderId, BigDecimal amount) { // todo Reservation

    public Payment toPayment(Reservation reservation) {
        return new Payment(paymentKey, amount, reservation);
    }
}
