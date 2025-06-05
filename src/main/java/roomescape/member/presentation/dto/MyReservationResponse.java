package roomescape.member.presentation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import roomescape.payment.domain.Payment;
import roomescape.reservation.infrastructure.dto.WaitingWithRank;
import roomescape.reservation.domain.Reservation;

public record MyReservationResponse(
    Long reservationId,
    String theme,
    LocalDate date,
    LocalTime time,
    String status,
    String paymentKey,
    String orderId,
    int amount
    ) {

    private static final String WAITING = "%d번째 예약대기";

    public static MyReservationResponse from(final Reservation reservation, Payment payment) {
        return new MyReservationResponse(
            reservation.getId(),
            reservation.getTheme().getName(),
            reservation.getDate(),
            reservation.getTime().getStartAt(),
            reservation.getStatus().getMessage(),
            payment.getPaymentKey(),
            payment.getOrderId(),
            payment.getAmount()
        );
    }

    public static MyReservationResponse from(final WaitingWithRank waitingWithRank) {
        Reservation reservation = waitingWithRank.getWaiting().getReservation();
        return new MyReservationResponse(
            reservation.getId(),
            reservation.getTheme().getName(),
            reservation.getDate(),
            reservation.getTime().getStartAt(),
            formatRank(waitingWithRank.getRank()),
            null,
            null,
            0
        );
    }

    private static String formatRank(Long rank) {
        return String.format(WAITING, rank);
    }
}
