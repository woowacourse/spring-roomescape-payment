package roomescape.reservation.service;

public record PaymentApprovalRequest(
       String paymentKey,
       String orderId,
       Long amount
) {
}
