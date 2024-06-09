package roomescape.controller.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import roomescape.domain.member.Member;
import roomescape.service.dto.request.ReservationCreateRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationRequest(
        @NotNull(message = "날짜를 입력해주세요.")
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
        BigDecimal amount
) {

    public ReservationCreateRequest toReservationCreateRequest(Member member) {
        return new ReservationCreateRequest(date, timeId, themeId, member, paymentKey, orderId, amount);
    }
}
