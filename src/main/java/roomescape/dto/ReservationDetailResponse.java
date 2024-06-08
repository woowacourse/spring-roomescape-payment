package roomescape.dto;

import roomescape.domain.ReservationStatus;
import roomescape.domain.Waiting;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationDetailResponse(
        long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        long amount
) {

    public static ReservationDetailResponse of(Reservation reservation, Payment payment) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                getStatusName(reservation.getStatus()),
                payment.getPaymentKey(),
                payment.getTotalAmount());
    }

    public static ReservationDetailResponse of(Waiting waiting, Payment payment) {
        Reservation reservation = waiting.getReservation();
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                String.format(getStatusName(reservation.getStatus()), waiting.getRank()),
                payment.getPaymentKey(),
                payment.getTotalAmount());
    }

    private static String getStatusName(ReservationStatus status) {
        return switch (status) {
            case BOOKED -> "예약";
            case WAITING -> "%d번째 예약대기";
        };
    }
}
