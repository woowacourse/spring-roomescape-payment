package roomescape.reservation.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record AdminReservationPaymentRequest(
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        long themeId,
        long timeId,
        String paymentKey,
        String orderId,
        long amount,
        String paymentType,
        long memberId
) {
}
