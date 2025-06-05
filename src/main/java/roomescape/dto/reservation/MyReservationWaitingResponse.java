package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationWithPayment;
import roomescape.domain.waiting.Waiting;
import roomescape.domain.waiting.WaitingWithRank;

public record MyReservationWaitingResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String status,
        String paymentKey,
        Long amount
) {

    public static MyReservationWaitingResponse from(ReservationWithPayment reservationWithPayment) {
        Reservation reservation = reservationWithPayment.reservation();
        return new MyReservationWaitingResponse(reservation.getId(), reservation.getTheme().getName(),
                reservation.getDate(), reservation.getTime().getStartAt(), "예약",
                reservationWithPayment.paymentKey(), reservationWithPayment.amount());
    }

    public static MyReservationWaitingResponse from(WaitingWithRank waitingWithRank) {
        Waiting waiting = waitingWithRank.waiting();
        return new MyReservationWaitingResponse(waiting.getId(), waiting.getTheme().getName(), waiting.getDate(),
                waiting.getTime().getStartAt(),
                waitingWithRank.rank() + "번째 예약대기", null, null);
    }
}
