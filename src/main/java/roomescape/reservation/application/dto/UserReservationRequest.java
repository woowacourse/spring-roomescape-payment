package roomescape.reservation.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UserReservationRequest(
        @NotNull(message = "예약 날짜는 필수 입력값입니다.")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate date,
        @NotNull(message = "예약 시간은 필수 입력값입니다.") Long timeId,
        @NotNull(message = "테마는 필수 입력값입니다.") Long themeId,
        @NotNull(message = "결제키는 필수 입력값입니다.") String paymentKey,
        @NotNull(message = "주문아이디는 필수 입력값입니다.") String orderId,
        @NotNull(message = "결제금액은 필수 입력값입니다.") BigDecimal amount
) {
}