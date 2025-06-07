package roomescape.presentation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import roomescape.application.request.PaymentInfo;

public record CreateReservationRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "예약 날짜를 선택해주세요.")
        LocalDate date,

        @NotNull(message = "예약 시간을 선택해주세요.")
        Long timeId,

        @NotNull(message = "테마를 선택해주세요.")
        Long themeId,

        @NotBlank(message = "결제 요청 키를 입력해주세요.")
        String paymentKey,

        @NotBlank(message = "주문 번호를 입력해주세요.")
        String orderId,

        @NotNull(message = "결제 금액을 입력해주세요.")
        @PositiveOrZero(message = "결제 금액은 음수일 수 없습니다.")
        Long amount,

        @NotBlank(message = "주문 명을 입력해주세요.")
        String orderName
) {

    public PaymentInfo toPaymentInfo() {
        return new PaymentInfo(
                paymentKey,
                orderId,
                orderName,
                amount
        );
    }
}
