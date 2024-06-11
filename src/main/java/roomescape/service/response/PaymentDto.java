package roomescape.service.response;

import roomescape.domain.payment.Payment;

public record PaymentDto(Long id, String paymentKey, String orderId, Long totalAmount) {

    private static final PaymentDto EMPTY_PAYMENT = new PaymentDto(null, null, null, null);

    public PaymentDto(String paymentKey, String orderId, Long totalAmount) {
        this(null, paymentKey, orderId, totalAmount);
    }

    private PaymentDto(Payment payment) {
        this(payment.getId(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getTotalAmount());
    }

    public static PaymentDto from(Payment payment) {
        if (payment == null) {
            return EMPTY_PAYMENT;
        }
        return new PaymentDto(payment);
    }
}
