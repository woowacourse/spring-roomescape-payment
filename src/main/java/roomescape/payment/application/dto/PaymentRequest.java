package roomescape.payment.application.dto;

public record PaymentRequest(
    Long reservationId,
    String paymentKey,
    String orderId,
    Long amount
) {

}
