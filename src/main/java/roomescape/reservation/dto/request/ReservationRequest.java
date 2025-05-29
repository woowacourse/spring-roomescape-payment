package roomescape.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record ReservationRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        Long amount) {

}
