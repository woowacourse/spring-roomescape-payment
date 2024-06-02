package roomescape.dto.reservation;

import java.time.LocalDate;

public record ReservationSaveRequest(
        LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        Long amount
) {
}
