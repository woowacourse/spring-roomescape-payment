package roomescape.reservation.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MemberReservationRequest(
        @NotNull LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount,
        @NotNull String paymentType
) {
}
