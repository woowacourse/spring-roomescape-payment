package roomescape.reservation.dto;

public record PaymentApprovalRequest(
       String paymentKey,
       String orderId,
       Long amount
) {
}
