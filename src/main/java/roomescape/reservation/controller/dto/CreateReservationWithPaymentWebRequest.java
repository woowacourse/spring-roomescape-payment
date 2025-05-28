package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import roomescape.reservation.controller.PaymentConfirmRequest;

public record CreateReservationWithPaymentWebRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        String paymentKey,
        String orderId,
        int amount
) {

    public PaymentConfirmRequest toPaymentConfirmRequest() {
        return new PaymentConfirmRequest(this.paymentKey, this.orderId, this.amount);
    }

    public CreateReservationWebRequest toCreateReservationWebRequest() {
        return new CreateReservationWebRequest(this.date, this.timeId, this.themeId);
    }
}
