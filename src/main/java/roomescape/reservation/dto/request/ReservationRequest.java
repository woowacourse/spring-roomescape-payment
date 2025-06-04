package roomescape.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.payment.dto.request.TossPaymentConfirmRequest;

import java.time.LocalDate;

public record ReservationRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        Long amount) {

    public TossPaymentConfirmRequest tossPaymentConfirmRequest() {
        return new TossPaymentConfirmRequest(this.orderId, this.amount, this.paymentKey);
    }
}
