package roomescape.service.response;

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
}
