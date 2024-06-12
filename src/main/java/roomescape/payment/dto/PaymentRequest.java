package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.dto.ReservationSaveRequest;

public record PaymentRequest(String orderId, int amount, String paymentKey) {

    public static PaymentRequest from(ReservationSaveRequest reservationSaveRequest) {
        return new PaymentRequest(
                reservationSaveRequest.getOrderId(),
                reservationSaveRequest.getAmount(),
                reservationSaveRequest.getPaymentKey()
        );
    }

    public Payment toPaymentStatusReady() {
        return new Payment(paymentKey, orderId, new BigDecimal(amount), PaymentStatus.READY);
    }
}
