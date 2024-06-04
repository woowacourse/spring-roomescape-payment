package roomescape.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.domain.reservation.slot.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeResponse(
        Long id,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        LocalTime startAt
) {
    public ReservationTimeResponse(ReservationTime reservationTime) {
        this(reservationTime.getId(), reservationTime.getStartAt());
    }
}
