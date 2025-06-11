package roomescape.reservation.application.dto.request;

import roomescape.reservation.presentation.dto.request.WaitingConfirmWebRequest;

public record WaitingConfirmRequest(Long reservationSlotId) {

    public static WaitingConfirmRequest of (WaitingConfirmWebRequest waitingConfirmWebRequest) {
        return new WaitingConfirmRequest(waitingConfirmWebRequest.reservationSlotId());
    }
}
