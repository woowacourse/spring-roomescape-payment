package roomescape.registration.domain.reservation.dto;

import java.time.LocalDate;

public record ReservationRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        String paymentType,
        String paymentKey,
        String orderId,
        Integer amount) {
}
