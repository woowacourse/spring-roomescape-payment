package roomescape.mvc.waiting.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record WaitingCreationRequest(
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
        @Positive(message = "결제 금액은 0보다 커야합니다.")
        Long amount
) {

}
