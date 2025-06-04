package roomescape.reservation.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import roomescape.payment.controller.dto.PaymentConfirmWebRequest;

public record CreateReservationWithPaymentWebRequest(
        @JsonProperty("reservation") CreateReservationWebRequest createReservationWebRequest,
        @JsonProperty("paymentConfirm") PaymentConfirmWebRequest paymentConfirmWebRequest
) {
}
