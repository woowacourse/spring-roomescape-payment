package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record UserReservationCreateRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        Long themeId,
        Long timeId,
        String paymentKey,
        String orderId,
        long amount
) {
}
