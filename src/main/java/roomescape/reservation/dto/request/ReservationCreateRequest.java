package roomescape.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationCreateRequest(
        @NotNull LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotNull PaymentDetail payment
) {
    // payment 객체를 나타내는 중첩 레코드
    public record PaymentDetail(
            @NotNull String paymentKey,
            @NotNull String orderId,
            @NotNull Long amount,
            @NotNull String paymentType
    ) {
    }
}
