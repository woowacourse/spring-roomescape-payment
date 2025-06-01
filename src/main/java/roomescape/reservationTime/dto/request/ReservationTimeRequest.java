package roomescape.reservationTime.dto.request;

import java.time.LocalTime;
import roomescape.common.exception.InvalidReservationException;

public record ReservationTimeRequest(LocalTime startAt) {
    public ReservationTimeRequest {
        if (startAt == null) {
            throw new InvalidReservationException("시간은 비어있을 수 없습니다.");
        }
    }
}
