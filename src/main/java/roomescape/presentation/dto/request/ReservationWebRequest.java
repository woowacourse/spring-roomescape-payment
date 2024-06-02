package roomescape.presentation.dto.request;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import roomescape.application.dto.request.ReservationRequest;

public record ReservationWebRequest(
        @NotNull(message = "날짜를 입력해주세요.")
        LocalDate date,

        @NotNull(message = "예약 시간 id을 입력해주세요.")
        @Positive
        Long timeId,

        @NotNull(message = "테마 id을 입력해주세요.")
        @Positive
        Long themeId,

        @NotNull(message = "payment key를 입력해주세요.")
        String paymentKey,

        @NotEmpty(message = "주문 id을 입력해주세요.")
        String orderId,

        @NotNull(message = "가격을 입력해주세요.")
        @Positive
        Long amount,

        @NotEmpty(message = "payment type을 입력해주세요.")
        String paymentType
) {

    public ReservationRequest toReservationRequest(Clock clock, Long memberId) {
        return new ReservationRequest(
                LocalDateTime.now(clock),
                date,
                themeId,
                timeId,
                paymentKey,
                orderId,
                amount,
                paymentType,
                memberId
        );
    }
}
