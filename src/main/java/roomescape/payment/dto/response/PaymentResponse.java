package roomescape.payment.dto.response;


public record PaymentResponse(String paymentKey, String orderId, String type,
                              String totalAmount, String status, String requestedAt) {
}
