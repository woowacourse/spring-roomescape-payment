package roomescape.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Waiting;
import roomescape.entity.Reservation;

@Schema(description = "관리자 예약 조회 응답 DTO 입니다.")
public record AdminReservationDetailResponse(
        @Schema(description = "예약 ID 입니다.")
        long id,
        @Schema(description = "예약 대기 숫자입니다.")
        long waitingNumber,
        @Schema(description = "예약자명입니다.")
        String member,
        @Schema(description = "테마명입니다.")
        String theme,
        @Schema(description = "예약 날짜입니다.")
        LocalDate date,
        @Schema(description = "예약 시간입니다.")
        LocalTime time
) {
    public static AdminReservationDetailResponse from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();
        return new AdminReservationDetailResponse(
                reservation.getId(),
                waiting.getRank(),
                reservation.getLoginMember().getName(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt());
    }
}
