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
        Long amount
) {

    public static ReservationDetailResponse from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();
        return toReservationDetailResponse(reservation, String.format(getStatusName(reservation.getStatus()), waiting.getRank()));
    }

    public static ReservationDetailResponse of(Reservation reservation, Payment payment) {
        if (reservation.isWaitingForPaymentStatus()) {
            return toReservationDetailResponse(reservation, getStatusName(reservation.getStatus()));
        }
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                getStatusName(reservation.getStatus()),
                payment.getPaymentKey(),
                payment.getTotalAmount());
    }

    private static ReservationDetailResponse toReservationDetailResponse(Reservation reservation, String status) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                status,
                null,
                null);
    }

    private static String getStatusName(ReservationStatus status) {
        return switch (status) {
            case BOOKED -> "예약";
            case WAITING -> "%d번째 예약대기";
            case WAITING_FOR_PAYMENT -> "결제 대기";
        };
    }
}
