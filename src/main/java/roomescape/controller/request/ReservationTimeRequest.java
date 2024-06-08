package roomescape.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nonnull;

import java.time.LocalTime;

public record ReservationTimeRequest(
        @Nonnull
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt) {
}
