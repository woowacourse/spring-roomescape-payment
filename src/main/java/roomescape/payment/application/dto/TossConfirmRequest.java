package roomescape.payment.application.dto;

public record TossConfirmRequest(
    String paymentKey,
    String orderId,
    Long amount
) {

}
