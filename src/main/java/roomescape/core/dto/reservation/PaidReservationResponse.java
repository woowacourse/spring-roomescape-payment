package roomescape.core.dto.reservation;

import roomescape.core.domain.PaidReservation;
import roomescape.core.dto.payment.PaymentConfirmResponse;

public class PaidReservationResponse {
    private final Long id;
    private final ReservationResponse reservationResponse;
    private final PaymentConfirmResponse paymentConfirmResponse;

    public PaidReservationResponse(final PaidReservation paidReservation) {
        this.id = paidReservation.getId();
        this.reservationResponse = new ReservationResponse(paidReservation.getReservation());
        this.paymentConfirmResponse = new PaymentConfirmResponse(paidReservation.getPayment());
    }

    public Long getId() {
        return id;
    }

    public ReservationResponse getReservationResponse() {
        return reservationResponse;
    }

    public PaymentConfirmResponse getPaymentConfirmResponse() {
        return paymentConfirmResponse;
    }
}
