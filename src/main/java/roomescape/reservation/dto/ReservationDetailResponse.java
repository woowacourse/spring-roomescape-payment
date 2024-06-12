package roomescape.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.Reservations;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.entity.Reservation;

@Schema(description = "자신의 예약 내역")
public record ReservationDetailResponse(
        @Schema(description = "예약 ID", example = "1")
        long reservationId,
        @Schema(description = "테마 이름", example = "테마 이름")
        String theme,
        @Schema(description = "예약 날짜", example = "#{T(java.time.LocalDate).now()}")
        LocalDate date,
        @Schema(description = "예약 시간", example = "23:00")
        LocalTime time,
        @Schema(description = "예약 상태", example = "예약")
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
        Reservation reservation = waiting.reservation();
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                String.format(getStatusName(reservation.getStatus()), waiting.rank()));
    }

    public static List<ReservationDetailResponse> of(Reservations reservations, List<Waiting> waitings) {
        List<ReservationDetailResponse> responses = new ArrayList<>();
        for (Reservation reservation : reservations.getReservations()) {
            ReservationDetailResponse response = waitings.stream()
                    .filter(waiting -> waiting.reservation().equals(reservation))
                    .findFirst()
                    .map(ReservationDetailResponse::from)
                    .orElse(from(reservation));
            responses.add(response);
        }
        return responses;
    }

    private static String getStatusName(ReservationStatus status) {
        return switch (status) {
            case BOOKED -> "예약";
            case WAITING -> "%d번째 예약대기";
        };
    }
}
