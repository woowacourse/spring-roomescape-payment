package roomescape.dto;

import roomescape.entity.Payment;

public record PaymentResponse(String paymentKey,
                              String orderName,
                              String requestedAt,
                              String approvedAt,
                              String currency,
                              long totalAmount) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentKey(),
                payment.getOrderName(),
                payment.getApprovedAt(),
                payment.getRequestedAt(),
                payment.getCurrency(),
                payment.getTotalAmount());
    }

    public Payment toModel(long reservationId) {
        return new Payment(reservationId, paymentKey, orderName, requestedAt, approvedAt, currency, totalAmount);
    }
}
