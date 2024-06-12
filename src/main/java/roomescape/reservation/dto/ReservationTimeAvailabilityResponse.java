package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalTime;
import roomescape.reservationtime.domain.ReservationTime;

@Tag(name = "예약 가능한 시간 응답", description = "시간 id, 방탈출 시작 시간과 함께 해당 시간의 방탈출 예약 여부를 함께 반환한다.")
public record ReservationTimeAvailabilityResponse(long timeId, LocalTime startAt, boolean alreadyBooked) {

    public static ReservationTimeAvailabilityResponse fromTime(ReservationTime time, boolean alreadyBooked) {
        return new ReservationTimeAvailabilityResponse(time.getId(), time.getStartAt(), alreadyBooked);
    }
}
