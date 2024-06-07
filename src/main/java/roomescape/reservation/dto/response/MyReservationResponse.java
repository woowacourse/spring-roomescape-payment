package roomescape.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        LocalDate date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount
) {

    public static MyReservationResponse from(Reservation reservation, Payment payment) {
        if (payment == null) {
            return createNonPaymentReservationResponse(reservation);
        }

        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                "예약",
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    private static MyReservationResponse createNonPaymentReservationResponse(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                "예약",
                null,
                null
        );
    }

    public static MyReservationResponse from(WaitingReservation waitingReservation) {
        return new MyReservationResponse(
                waitingReservation.getReservation().getId(),
                waitingReservation.getReservation().getTheme().getName(),
                waitingReservation.getReservation().getDate(),
                waitingReservation.getReservation().getTime().getStartAt(),
                (waitingReservation.calculateOrder()) + "번째 예약대기",
                null,
                null
        );
    }
}
