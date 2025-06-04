package roomescape.timeslot.dto.request;

import java.time.LocalTime;

public record TimeSlotRequest(LocalTime startAt) {

    public TimeSlotRequest {
        if (startAt == null) {
            throw new IllegalArgumentException("time은 null 일 수 없습니다.");
        }
    }
}
