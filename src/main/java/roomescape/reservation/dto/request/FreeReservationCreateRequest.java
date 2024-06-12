package roomescape.reservation.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record FreeReservationCreateRequest(
        @NotNull LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId

) {
}
