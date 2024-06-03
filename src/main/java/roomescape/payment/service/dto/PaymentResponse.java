package roomescape.payment.service.dto;

public record PaymentResponse(
        String paymentKey,
        String status,
        String orderId,
        Long totalAmount,
        String method

) {
}
