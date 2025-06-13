package roomescape.payment.application.dto;

public record PaymentRequest(
    String paymentKey,
    String orderId,
    Long amount
) {

}
