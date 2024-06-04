package roomescape.service.response;

import roomescape.domain.ReservationPayment;

public record ReservationPaymentDto(
        ReservationDto reservationDto,
        PaymentDto paymentDto) {

    public ReservationPaymentDto(ReservationPayment reservationPayment) {
        this(
                new ReservationDto(reservationPayment.getReservation()),
                new PaymentDto(reservationPayment)
        );
    }
}
