package roomescape.payment.application.dto;

public record PaymentResponse(
        String orderId,
        String paymentKey,
        Long totalAmount
) {

}
