package roomescape.reservation.dto.request;

import java.time.LocalDate;

public record ReservationRequest(LocalDate date, Long timeId, Long themeId, String paymentKey, String orderId, Long amount) {
    public ReservationRequest {
    }
}
