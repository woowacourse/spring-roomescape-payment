package roomescape.order.dto;

public record PaymentConfirmRequest(
        String orderId,
        Long amount,
        String paymentKey
) {

}
