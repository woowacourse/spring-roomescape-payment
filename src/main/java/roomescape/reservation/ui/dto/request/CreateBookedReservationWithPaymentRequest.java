package roomescape.reservation.ui.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateBookedReservationWithPaymentRequest(

        @NotNull
        LocalDate date,
        @NotNull
        Long timeId,
        @NotNull
        Long themeId,
        @NotNull
        String paymentKey,
        @NotNull
        String orderId,
        @NotNull
        Long amount
) {

}
