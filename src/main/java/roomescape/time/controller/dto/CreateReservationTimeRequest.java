package roomescape.time.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record CreateReservationTimeRequest(
        @NotNull LocalTime startAt
) {
}
