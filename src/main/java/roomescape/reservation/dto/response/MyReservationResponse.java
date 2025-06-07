package roomescape.reservation.dto.response;

import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

public record MyReservationResponse(Long reservationId,
                                    String theme,
                                    String date,
                                    String time,
                                    String reservedStatus,
                                    String paymentKey,
                                    Integer amount) {

    public static MyReservationResponse from(final WaitingWithRank waitingWithRank) {
        return new MyReservationResponse(waitingWithRank.getWaiting().getId(),
                waitingWithRank.getWaiting().getTheme().getName(),
                waitingWithRank.getWaiting().getDate().toString(),
                waitingWithRank.getWaiting().getTime().getStartAt().toString(),
                waitingWithRank.getRank() + "번째 " + ReservationStatus.WAITING.getName(),
                null,
                null
        );
    }


    public static MyReservationResponse from(final Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate().toString(),
                reservation.getTime().getStartAt().toString(),
                ReservationStatus.RESERVED.getName(),
                null,
                null
        );
    }

    public static MyReservationResponse from(final Reservation reservation, final Payment payment) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate().toString(),
                reservation.getTime().getStartAt().toString(),
                ReservationStatus.RESERVED.getName(),
                payment.getPaymentKey(),
                payment.getTotalAmount()
        );
    }
}
