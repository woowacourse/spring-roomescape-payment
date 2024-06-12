package roomescape.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.TimeSlot;

@Schema(description = "Time Slot Request Model")
public record TimeSlotRequest(@Schema(description = "Start time of the time slot", example = "14:30")
                              @JsonFormat(pattern = "HH:mm")
                              LocalTime startAt) {

    public TimeSlotRequest {
        isValid(startAt);
    }

    public TimeSlot toEntity() {
        return new TimeSlot(startAt);
    }

    private void isValid(LocalTime startAt) {
        if (startAt == null) {
            throw new IllegalArgumentException("[ERROR] 잘못된 시간입니다");
        }
    }
}
