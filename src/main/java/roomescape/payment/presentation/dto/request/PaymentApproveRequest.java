package roomescape.payment.presentation.dto.request;

import roomescape.reservation.presentation.dto.request.ConfirmedReservationCreateWebRequest;
import roomescape.reservation.presentation.dto.request.WaitingConfirmWebRequest;

public record PaymentApproveRequest(String paymentKey, String orderId, Long amount) {

    public static PaymentApproveRequest from(final ConfirmedReservationCreateWebRequest confirmedReservationCreateWebRequest) {
        return new PaymentApproveRequest(confirmedReservationCreateWebRequest.paymentKey(), confirmedReservationCreateWebRequest.orderId(),
                confirmedReservationCreateWebRequest.amount());
    }

    public static PaymentApproveRequest from(WaitingConfirmWebRequest waitingConfirmWebRequest) {
        return new PaymentApproveRequest(waitingConfirmWebRequest.paymentKey(), waitingConfirmWebRequest.orderId(), waitingConfirmWebRequest.amount());
    }
}
