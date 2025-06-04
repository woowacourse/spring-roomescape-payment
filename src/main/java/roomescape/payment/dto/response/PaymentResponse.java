package roomescape.payment.dto.response;


import roomescape.payment.domain.Payment;

public record PaymentResponse(String paymentKey, String orderId, String type,
                              Integer totalAmount, String status, String requestedAt) {
    public Payment toEntity() {
        return new Payment(paymentKey, orderId, type, totalAmount, status, requestedAt);
    }
}
