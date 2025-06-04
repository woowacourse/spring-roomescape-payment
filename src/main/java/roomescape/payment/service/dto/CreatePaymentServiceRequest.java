package roomescape.payment.service.dto;

public record CreatePaymentServiceRequest(
        String paymentKey,
        String orderId,
        int amount
) {
}
