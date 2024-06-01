package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record UserReservationPaymentRequest(
        LocalDate date,
        Long timeId,
        Long themeId,
        Long memberId,
        String paymentKey,
        String orderId,
        int amount,
        String paymentType
) {
}
