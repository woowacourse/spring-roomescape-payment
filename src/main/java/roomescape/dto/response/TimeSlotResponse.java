package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.TimeSlot;

@Schema(description = "Time Slot Response Model")
public record TimeSlotResponse(@Schema(description = "Time Slot ID", example = "456")
                               Long id,

                               @Schema(description = "Start time of the time slot", example = "10:00")
                               @JsonFormat(pattern = "HH:mm")
                               LocalTime startAt) {

    public static TimeSlotResponse from(TimeSlot timeSlot) {
        return new TimeSlotResponse(
                timeSlot.getId(),
                timeSlot.getStartAt()
        );
    }
}
