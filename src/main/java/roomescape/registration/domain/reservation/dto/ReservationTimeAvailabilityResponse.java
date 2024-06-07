package roomescape.registration.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalTime;

@Schema(description = "예약 시간 가능 여부 응답")
public record ReservationTimeAvailabilityResponse(

        @Schema(description = "시간 ID", example = "2")
        long timeId,

        @Schema(description = "시작 시간", example = "14:00")
        LocalTime startAt,

        @Schema(description = "이미 예약됨", example = "true")
        boolean alreadyBooked) {

    public static ReservationTimeAvailabilityResponse fromTime(ReservationTime time, boolean alreadyBooked) {
        return new ReservationTimeAvailabilityResponse(time.getId(), time.getStartAt(), alreadyBooked);
    }
}
