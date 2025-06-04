package roomescape.reservation.dto.response;

import roomescape.reservation.domain.Reservation;

public record MyReservationResponse(
        Long id,
        String theme,
        String date,
        String time,
        String status,
        String paymentKey,
        long amount
) {
    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate().toString(),
                reservation.getReservationTime().toString(),
                "예약",
                reservation.getPayment().getPaymentKey(),
                reservation.getPayment().getAmount()
        );
    }
}
