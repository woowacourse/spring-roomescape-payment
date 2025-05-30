package roomescape.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import roomescape.reservation.dto.ReservationRequest;

public record ReservationPaymentRequest(
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId,
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @NotNull @Positive Long amount,
        @NotNull String paymentType
) {
    public ReservationRequest toReservationRequest() {
        return new ReservationRequest(date, timeId, themeId);
    }
}
