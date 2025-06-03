package roomescape.payment.presentation.dto.request;

import roomescape.reservation.presentation.dto.request.ConfirmedReservationCreateWebRequest;

public record PaymentApproveRequest(String paymentKey, String orderId, Long amount) {

    public static PaymentApproveRequest from(final ConfirmedReservationCreateWebRequest confirmedReservationCreateWebRequest) {
        return new PaymentApproveRequest(confirmedReservationCreateWebRequest.paymentKey(), confirmedReservationCreateWebRequest.orderId(),
                confirmedReservationCreateWebRequest.amount());
    }
}
