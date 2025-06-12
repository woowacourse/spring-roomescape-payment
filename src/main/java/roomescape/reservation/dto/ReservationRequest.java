package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationRequest(
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull long amount
) {
}
