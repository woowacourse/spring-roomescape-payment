package roomescape.reservation.service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record FilteringReservationRequest(
        @NotNull
        Long themeId,
        @NotNull
        Long memberId,
        @Past
        LocalDate dateFrom,
        @PastOrPresent
        LocalDate dateTo
) {
}
