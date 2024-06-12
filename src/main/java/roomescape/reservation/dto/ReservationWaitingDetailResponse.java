package roomescape.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.entity.Reservation;

@Schema(description = "예약 대기 내역")
public record ReservationWaitingDetailResponse(
        @Schema(description = "예약 ID", example = "2")
        long id,
        @Schema(description = "예약 대기 순번", example = "1")
        long waitingNumber,
        @Schema(description = "예약 대기자 명", example = "admin")
        String member,
        @Schema(description = "테마 이름", example = "테마 이름")
        String theme,
        @Schema(description = "예약 날짜", example = "#{T(java.time.LocalDate).now()}")
        LocalDate date,
        @Schema(description = "예약 시간", example = "23:00")
        LocalTime time
) {
    public static ReservationWaitingDetailResponse from(Waiting waiting) {
        Reservation reservation = waiting.reservation();
        return new ReservationWaitingDetailResponse(
                reservation.getId(),
                waiting.rank(),
                reservation.getLoginMember().getName(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt());
    }
}
