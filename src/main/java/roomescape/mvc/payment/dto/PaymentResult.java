package roomescape.mvc.payment.dto;

public record PaymentResult(
        String orderId,
        String paymentKey,
        Long totalAmount
) {

}
