package roomescape.dto.response;

public record RefundPaymentResponse(
        String paymentKey,
        String orderId
) {
}
