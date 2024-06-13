package roomescape.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import roomescape.service.dto.request.CreateReservationRequest;
import roomescape.service.dto.request.PaymentRequest;

public record ReservationRequest(
        @NotNull(message = "날짜를 입력해주세요.")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @NotNull(message = "예약 시간 id을 입력해주세요.")
        @Positive
        Long timeId,

        @NotNull(message = "테마 id을 입력해주세요.")
        @Positive
        Long themeId,

        @NotBlank(message = "paymentKey를 입력해주세요.")
        String paymentKey,

        @NotBlank(message = "orderId를 입력해주세요.")
        String orderId,

        @NotNull(message = "결제 금액을 입력해주세요.")
        @Positive(message = "결제 금액은 양수만 가능합니다.")
        @Max(value = Integer.MAX_VALUE, message = "결제 금액은 2,147,483,647 이하여야 합니다.")
        Integer amount
) {

    public CreateReservationRequest toCreateReservationRequest(long memberId) {
        return new CreateReservationRequest(date, timeId, themeId, memberId);
    }

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(paymentKey, orderId, amount);
    }
}
