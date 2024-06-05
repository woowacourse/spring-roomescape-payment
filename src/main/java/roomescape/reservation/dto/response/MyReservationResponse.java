package roomescape.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPayment;
import roomescape.reservation.domain.WaitingReservation;

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
        Long amount
) {

    public static MyReservationResponse from(ReservationPayment reservationPayment) {
        Reservation reservation = reservationPayment.reservation();
        Payment payment = reservationPayment.payment();
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                "예약",
                payment.getPaymentKey(),
                payment.getTotalAmount()
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
