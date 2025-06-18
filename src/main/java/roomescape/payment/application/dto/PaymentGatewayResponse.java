package roomescape.payment.application.dto;

public record PaymentGatewayResponse(
        String paymentKey,
        String orderId,
        Long amount
) {
}
