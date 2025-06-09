package roomescape.domain.payment.dto;

public record PaymentResult(
        String orderId,
        String paymentKey,
        Long totalAmount
) {

}
