package roomescape.reservation.presentation.dto.request;

public record WaitingConfirmWebRequest(Long reservationSlotId,
                                       String paymentKey,
                                       String orderId,
                                       Long amount) {
}
