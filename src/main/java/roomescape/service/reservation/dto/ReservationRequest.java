package roomescape.service.reservation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationRequest(
        LocalDate date,
        @NotNull(message = "시간 ID를 입력해주세요.") Long timeId,
        @NotNull(message = "테마 ID를 입력해주세요.") Long themeId,
        // TODO: 결제정보 검증
        String paymentKey,
        String orderId,
        long amount,
        String paymentType
) {

    public ReservationRequest(LocalDate date, Long timeId, Long themeId) {
        this(date, timeId, themeId, "", "", 0, "");
    }
}
