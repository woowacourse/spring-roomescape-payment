package roomescape.reservation.controller.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ReservePaymentRequest(@NotNull(message = "예약 날짜를 입력해주세요.") LocalDate date,
                                    @NotNull(message = "예약 시간을 선택해주세요.") Long timeId,
                                    @NotNull(message = "테마를 선택해주세요.") Long themeId,
                                   @NotNull(message = "결제 정보를 입력해주세요.") PaymentInfoRequest payment) {
}
