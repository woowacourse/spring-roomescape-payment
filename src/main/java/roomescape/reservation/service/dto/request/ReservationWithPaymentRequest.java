package roomescape.reservation.service.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(name = "ReservationWithPaymentRequest(결제 포함 예약 생성 요청 DTO)")
public record ReservationWithPaymentRequest(
        @FutureOrPresent
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotEmpty String paymentKey,
        @NotEmpty String orderId,
        @NotNull Integer amount,
        @NotNull String pgType
) {
}
