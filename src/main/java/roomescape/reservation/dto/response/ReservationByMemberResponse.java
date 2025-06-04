package roomescape.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.entity.Reservation;
import roomescape.waiting.entity.Waiting;
import roomescape.waiting.entity.WaitingWithRank;

public record ReservationByMemberResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        Long amount
) {
    public static ReservationByMemberResponse of(Reservation reservation, Payment payment) {
        return new ReservationByMemberResponse(
                reservation.getId(),
                reservation.getReservationSlot().getTheme().getName(),
                reservation.getReservationSlot().getDate(),
                reservation.getReservationSlot().getTime().getStartAt(),
                "예약",
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static ReservationByMemberResponse from(Reservation reservation) {
        return new ReservationByMemberResponse(
                reservation.getId(),
                reservation.getReservationSlot().getTheme().getName(),
                reservation.getReservationSlot().getDate(),
                reservation.getReservationSlot().getTime().getStartAt(),
                "예약",
                "",
                0L
        );
    }

    public static ReservationByMemberResponse from(WaitingWithRank waitingWithRank) {
        Waiting waiting = waitingWithRank.getWaiting();
        return new ReservationByMemberResponse(
                waiting.getId(),
                waiting.getReservationSlot().getTheme().getName(),
                waiting.getReservationSlot().getDate(),
                waiting.getReservationSlot().getTime().getStartAt(),
                String.format("%d번째 예약대기", waitingWithRank.getRank() + 1),
                "",
                0L
        );
    }
}
