package roomescape.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import roomescape.service.dto.request.CreateReservationRequest;

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
        @Positive
        Integer amount
) {

    public CreateReservationRequest toCreateReservationRequest(long memberId) {
        return new CreateReservationRequest(date, timeId, themeId, memberId);
    }
}
