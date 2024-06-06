package roomescape.application.dto.request.reservation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import roomescape.application.dto.request.payment.PaymentRequest;

public record UserReservationRequest(
        @Future(message = "과거에 대한 예약은 할 수 없습니다.")
        @NotNull(message = "날짜에 빈값은 허용하지 않습니다.") LocalDate date,
        @Positive(message = "타임 아이디는 1이상의 정수만 허용합니다.") Long timeId,
        @Positive(message = "테마 아이디는 1이상의 정수만 허용합니다.") Long themeId,
        Long amount,
        String orderId,
        String paymentKey,
        String paymentType
) {

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(
                amount,
                orderId,
                paymentKey
        );
    }
}
