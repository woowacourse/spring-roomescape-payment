package roomescape.payment.dto.request;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        Long amount
) {

}
