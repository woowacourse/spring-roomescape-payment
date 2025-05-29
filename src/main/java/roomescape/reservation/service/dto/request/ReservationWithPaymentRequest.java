package roomescape.reservation.service.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationWithPaymentRequest(
        @FutureOrPresent
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        @NotNull Long timeId,
        @NotNull Long themeId,
        @NotEmpty String paymentKey,
        @NotEmpty String orderId,
        @NotNull Integer amount
) {
}
