package roomescape.domain.reservation.reserved;

import jakarta.annotation.Nullable;
import java.time.LocalDate;

public record ReservedSearchFilter(@Nullable Long themeId, @Nullable Long userId, @Nullable LocalDate dateFrom,
                                   @Nullable LocalDate dateTo) {
}
