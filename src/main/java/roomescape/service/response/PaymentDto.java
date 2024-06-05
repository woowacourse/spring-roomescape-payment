package roomescape.service.response;

import roomescape.domain.Payment;
import roomescape.domain.ReservationPayment;

public record PaymentDto(Long id, String paymentKey, String orderId, Long totalAmount) {

    public PaymentDto(String paymentKey, String orderId, Long totalAmount) {
        this(null, paymentKey, orderId, totalAmount);
    }

    public PaymentDto(ReservationPayment reservationPayment) {
        this(reservationPayment.getPaymentId(),
                reservationPayment.getPaymentKey(),
                reservationPayment.getOrderId(),
                reservationPayment.getTotalAmount());
    }

    public PaymentDto(Payment payment) {
        this(payment.getId(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getTotalAmount());
    }
}
