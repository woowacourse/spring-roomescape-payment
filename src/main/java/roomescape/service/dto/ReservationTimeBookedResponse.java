package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.reservation.slot.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeBookedResponse(
        Long id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        LocalTime startAt,
        boolean alreadyBooked
) {
    public static ReservationTimeBookedResponse of(ReservationTime time, boolean alreadyBooked) {
        return new ReservationTimeBookedResponse(time.getId(), time.getStartAt(), alreadyBooked);
    }
}
