package roomescape.reservation.service.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record FilteringReservationRequest(
        @Nullable
        Long themeId,
        @Nullable
        Long memberId,
        @Nullable
        @Past
        LocalDate dateFrom,
        @Nullable
        @PastOrPresent
        LocalDate dateTo
) {
}
