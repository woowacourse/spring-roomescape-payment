package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

public record ReservationWithStatus(long reservationId, String themeName, LocalDate date, LocalTime time,
                                    ReservationStatus status, String paymentKey, Long amount) {
    public static ReservationWithStatus of(Reservation reservation, Payment payment) {
        return new ReservationWithStatus(
                reservation.getId(),
                reservation.getReservationSlot().getTheme().getName(),
                reservation.getReservationSlot().getDate(),
                reservation.getReservationSlot().getTime().getStartAt(),
                reservation.getStatus(),
                payment.getPaymentKey(),
                payment.getTotalAmount());
    }
}
