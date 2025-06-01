package roomescape.reservation.external.dto.respone;

public record PaymentApproveResponse(
        String paymentKey,
        String orderId
) {
}
