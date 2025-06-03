package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import roomescape.payment.PaymentRequest;

@Builder
@Jacksonized
public record ReservationPaymentRequest(
        @JsonUnwrapped @Valid ReservationRequest reservationRequest,
        @JsonUnwrapped @Valid PaymentRequest paymentRequest
) {
}
