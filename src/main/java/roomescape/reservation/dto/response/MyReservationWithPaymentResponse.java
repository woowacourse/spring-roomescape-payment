package roomescape.reservation.dto.response;

import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.Waiting;

public record MyReservationWithPaymentResponse(
        Long id,
        String theme,
        String date,
        String time,
        String status,
        String paymentKey,
        String amount
) {
    public static MyReservationWithPaymentResponse from(Reservation reservation) {
        return new MyReservationWithPaymentResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate().toString(),
                reservation.getReservationTime().toString(),
                "예약",
                reservation.getPayment().getPaymentKey(),
                reservation.getPayment().getAmount().toString()
        );
    }

    public static MyReservationWithPaymentResponse fromWaiting(Waiting waiting, long rank) {
        return new MyReservationWithPaymentResponse(
                waiting.getId(),
                waiting.getTheme().getName(),
                waiting.getDate().toString(),
                waiting.getTime().getStartAt().toString(),
                String.valueOf(rank),
                "",
                ""
        );
    }
}
