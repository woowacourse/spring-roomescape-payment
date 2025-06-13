package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.waiting.Waiting;
import roomescape.domain.waiting.WaitingWithRank;

public record MyReservationAndWaitingsResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String status,
        String paymentKey,
        Long amount
) {

    public static MyReservationAndWaitingsResponse of(Reservation reservation, Payment payment) {
        if (payment == null) {
            return MyReservationAndWaitingsResponse.from(reservation);
        }
        return new MyReservationAndWaitingsResponse(reservation.getId(), reservation.getTheme().getName(),
                reservation.getDate(), reservation.getTime().getStartAt(), "예약", payment.getPaymentKey(),
                payment.getAmount());
    }

    public static MyReservationAndWaitingsResponse from(Reservation reservation) {
        return new MyReservationAndWaitingsResponse(reservation.getId(), reservation.getTheme().getName(),
                reservation.getDate(), reservation.getTime().getStartAt(), "예약", null, null);
    }

    public static MyReservationAndWaitingsResponse from(WaitingWithRank waitingWithRank) {
        Waiting waiting = waitingWithRank.getWaiting();
        return new MyReservationAndWaitingsResponse(waiting.getId(), waiting.getTheme().getName(), waiting.getDate(),
                waiting.getTime().getStartAt(),
                waitingWithRank.getRank() + "번째 예약대기", null, null);
    }
}
