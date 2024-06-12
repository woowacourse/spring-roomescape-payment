package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

@Schema(description = "Booking Response Model")
public record BookResponse(@Schema(description = "Start time of the booking", example = "14:00")
                           @JsonFormat(pattern = "HH:mm")
                           LocalTime startAt,

                           @Schema(description = "ID of the time slot", example = "1")
                           Long timeId,

                           @Schema(description = "Flag indicating if the time slot is already booked", example = "true")
                           Boolean alreadyBooked) {
}

