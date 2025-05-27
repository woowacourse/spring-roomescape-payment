package roomescape.dto.business;

public record PaymentResult(
        String orderId,
        String paymentKey,
        String paymentType
) {

}
