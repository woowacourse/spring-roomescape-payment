package roomescape.service.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationRequest(
        LocalDate date,
        @NotNull(message = "시간 ID를 입력해주세요.") Long timeId,
        @NotNull(message = "테마 ID를 입력해주세요.") Long themeId,
        @NotBlank(message = "결제 키가 유효하지 않습니다.") String paymentKey,
        @NotBlank(message = "주문 번호가 유효하지 않습니다.") String orderId,
        long amount,
        @NotBlank(message = "결제 수단이 유효하지 않습니다.") String paymentType
) {

    public ReservationRequest(LocalDate date, Long timeId, Long themeId) {
        this(date, timeId, themeId, "", "", 0, "");
    }
}
