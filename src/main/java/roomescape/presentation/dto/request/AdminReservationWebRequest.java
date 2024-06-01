package roomescape.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import roomescape.application.dto.request.PaymentRequest;
import roomescape.application.dto.request.ReservationRequest;

public record AdminReservationWebRequest(
        @NotNull(message = "날짜는 필수 값입니다.")
        LocalDate date,

        @NotNull(message = "테마 id는 필수 값입니다.")
        @Positive
        Long themeId,

        @NotNull(message = "시간 id는 필수 값입니다.")
        @Positive
        Long timeId,

        @NotNull(message = "payment key를 입력해주세요.")
        String paymentKey,

        @NotNull(message = "주문 id을 입력해주세요.")
        String orderId,

        @NotNull(message = "가격을 입력해주세요.")
        @Positive
        BigDecimal amount,

        @NotNull(message = "payment type을 입력해주세요.")
        String paymentType,

        @NotNull(message = "회원 id는 필수 값입니다.")
        @Positive
        Long memberId
) {

    public ReservationRequest toReservationRequest(Clock clock) {
        PaymentRequest payment = new PaymentRequest(paymentKey, orderId, amount, paymentType);

        return new ReservationRequest(
                LocalDateTime.now(clock),
                date,
                themeId,
                timeId,
                payment,
                memberId
        );
    }
}
