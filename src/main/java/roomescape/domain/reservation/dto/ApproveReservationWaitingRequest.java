package roomescape.domain.reservation.dto;

public record ApproveReservationWaitingRequest(
        Long reservationWaitingId,
        String orderId,
        Long amount,
        String paymentKey
) {
}
