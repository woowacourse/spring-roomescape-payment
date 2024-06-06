package roomescape.dto.response.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.reservationwaiting.ReservationWaitingWithRank;

public record MyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount
) {
    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                reservation.getStatus().getValue(),
                reservation.getPayment().getPaymentKey(),
                reservation.getPayment().getTotalAmount()
        );
    }

    public static MyReservationResponse from(ReservationWaitingWithRank reservationWaitingWithRank) {
        ReservationWaiting reservationWaiting = reservationWaitingWithRank.getReservationWaiting();
        return new MyReservationResponse(
                reservationWaiting.getId(),
                reservationWaiting.getTheme().getName(),
                reservationWaiting.getDate(),
                reservationWaiting.getTime().getStartAt(),
                reservationWaitingWithRank.getRank() + 1 + "번째 예약 대기",
                null,
                null
        );
    }
}
