package roomescape.dto;

import roomescape.domain.ReservationStatus;
import roomescape.domain.Waiting;
import roomescape.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

public record ReservationDetailResponse(
        long reservationId,
        String theme,
        LocalDate date,
        LocalTime time,
        String status
) {
    public static ReservationDetailResponse from(Reservation reservation) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                getStatusName(reservation.getStatus()));
    }

    public static ReservationDetailResponse from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                String.format(getStatusName(reservation.getStatus()), waiting.getRank()));
    }

    public static List<ReservationDetailResponse> of(List<Reservation> bookedReservations, List<Waiting> waitings) {
        return Stream.concat(
                        bookedReservations.stream().map(ReservationDetailResponse::from),
                        waitings.stream().map(ReservationDetailResponse::from)
                )
                .toList();
    }

    private static String getStatusName(ReservationStatus status) {
        return switch (status) {
            case BOOKED -> "예약";
            case WAITING -> "%d번째 예약대기";
        };
    }
}
