package roomescape.reservation.dto.response;

public record PaymentApproveResponse(
        String paymentKey,
        String orderId
) {
}
