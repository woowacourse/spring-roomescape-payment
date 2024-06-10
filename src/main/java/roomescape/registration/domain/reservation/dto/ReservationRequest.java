package roomescape.registration.domain.reservation.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;

@Tag(name = "예약 요청", description = "예약시 필요한 방탈출 예약 정보 & 결제 정보를 요청한다.")
public record ReservationRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        String paymentType,
        String paymentKey,
        String orderId,
        BigDecimal amount) {
}
