package roomescape.domain.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationWithRank(
        long reservationId,
        String themeName,
        LocalDate date,
        LocalTime time,
        Status status,
        String paymentKey,
        BigDecimal amount,
        long waitingRank) {
}
