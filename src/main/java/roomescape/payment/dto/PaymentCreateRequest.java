package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.reservation.domain.Reservation;

public record PaymentCreateRequest(String paymentKey, String orderId, BigDecimal amount, Reservation reservation) {
    public RestClientPaymentApproveRequest createRestClientPaymentApproveRequest() {
        return new RestClientPaymentApproveRequest(paymentKey, orderId, amount);
    }
}
