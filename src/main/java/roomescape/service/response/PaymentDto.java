package roomescape.service.response;

import roomescape.domain.PaidReservation;

public record PaymentDto(Long id, String paymentKey, String orderId, Long totalAmount) {

    public PaymentDto(String paymentKey, String orderId, Long totalAmount) {
        this(null, paymentKey, orderId, totalAmount);
    }

    public PaymentDto(PaidReservation paidReservation) {
        this(paidReservation.getPaymentId(),
                paidReservation.getPaymentKey(),
                paidReservation.getOrderId(),
                paidReservation.getTotalAmount());
    }
}
