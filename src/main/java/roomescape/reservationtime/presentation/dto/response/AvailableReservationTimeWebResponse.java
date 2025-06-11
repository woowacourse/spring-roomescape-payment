package roomescape.reservationtime.presentation.dto.response;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservationtime.domain.ReservationTime;

public record AvailableReservationTimeWebResponse(
        @Schema(description = "예약시간 엔티티의 기본 키") Long timeId,
        @Schema(description = "예약 가능한 시간") LocalTime startAt,
        @Schema(description = "예약시간의 현재 예약 가능 여부") boolean alreadyBooked
) {
    public static AvailableReservationTimeWebResponse of(ReservationTime reservationTime, boolean alreadyBooked) {
        return new AvailableReservationTimeWebResponse(
                reservationTime.getId(),
                reservationTime.getStartAt(),
                alreadyBooked
        );
    }
}
