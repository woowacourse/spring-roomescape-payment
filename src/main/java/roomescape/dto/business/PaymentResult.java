package roomescape.dto.business;

public record PaymentResult(
        String orderId,
        String paymentKey,
        Long totalAmount
) {

}
