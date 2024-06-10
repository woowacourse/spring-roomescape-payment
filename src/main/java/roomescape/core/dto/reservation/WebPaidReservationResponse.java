package roomescape.core.dto.reservation;

import roomescape.core.domain.Reservation;
import roomescape.core.dto.payment.PaymentResponse;

public class WebPaidReservationResponse {
    private final ReservationResponse reservationResponse;
    private final PaymentResponse paymentResponse;

    public WebPaidReservationResponse(final Reservation reservation) {
        this.reservationResponse = new ReservationResponse(reservation);
        this.paymentResponse = new PaymentResponse(reservation.getPayment().get());
    }

    public ReservationResponse getReservationResponse() {
        return reservationResponse;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }
}
