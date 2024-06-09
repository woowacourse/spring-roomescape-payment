package roomescape.service.dto.request;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

import java.math.BigDecimal;

public record PaymentRequest(String paymentKey, String orderId,
                             BigDecimal amount) { // todo Reservation, 이름 PaymentConfirmRequest로 변경

    public Payment toPayment(Reservation reservation) {
        return Payment.tossPay(paymentKey, amount, reservation);
    }
}
