package roomescape.payment.dto.response;


public record PaymentResponse(String paymentKey, String orderId, String type,
                              Integer totalAmount, String status, String requestedAt) {
}
