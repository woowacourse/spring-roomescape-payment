package roomescape.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateReservationRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent(message = "날짜는 현재보다 미래여야합니다.")
        LocalDate date,
        @NotNull(message = "예약 시간 ID는 필수입니다.") Long timeId,
        @NotNull(message = "테마 ID는 필수입니다.") Long themeId,
        @NotBlank(message = "payment key 는 필수입니다.") String paymentKey,
        @NotBlank(message = "order ID 는 필수입니다.") String orderId,
        @Positive(message = "금액은 음수가 될 수 없습니다.") int amount
        ) {

}
