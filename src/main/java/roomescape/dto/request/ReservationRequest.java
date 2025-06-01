package roomescape.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReservationRequest(
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        long themeId,
        long timeId,
        String paymentKey,
        String orderId,
        int amount,
        String paymentType
) {
}
