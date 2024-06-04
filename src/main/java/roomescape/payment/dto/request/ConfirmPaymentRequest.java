package roomescape.payment.dto.request;

import roomescape.payment.model.Payment;
import roomescape.reservation.dto.request.CreateMyReservationRequest;
import roomescape.reservation.model.Reservation;

public record ConfirmPaymentRequest(String paymentKey,
                                    String orderId,
                                    Long amount) {
    public static ConfirmPaymentRequest from(final CreateMyReservationRequest createMyReservationRequest) {
        return new ConfirmPaymentRequest(
                createMyReservationRequest.paymentKey(),
                createMyReservationRequest.orderId(),
                createMyReservationRequest.amount());
    }
}
