package roomescape.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationWithPaymentCreationRequest(
        @NotNull(message = "테마ID는 빈 값을 허용하지 않습니다.")
        Long themeId,

        @NotNull(message = "날짜는 빈 값을 허용하지 않습니다.")
        LocalDate date,

        @NotNull(message = "예약시간ID는 빈 값을 허용하지 않습니다.")
        Long timeId,

        @NotNull(message = "주문ID는 빈 값을 허용하지 않습니다.")
        String orderId,

        @NotNull(message = "결제 키는 빈 값을 허용하지 않습니다.")
        String paymentKey,

        @NotNull(message = "결제 유형은 빈 값을 허용하지 않습니다.")
        String paymentType,

        @NotNull(message = "결제 금액은 빈 값을 허용하지 않습니다.")
        Integer amount
) {

}
