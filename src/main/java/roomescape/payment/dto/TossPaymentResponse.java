package roomescape.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentCurrency;
import roomescape.payment.domain.PaymentMethod;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentResponse(String paymentKey, String orderId, String method, PaymentCurrency currency, BigDecimal totalAmount) {

    public Payment from(Reservation reservation) {
        return new Payment(reservation, paymentKey, orderId, PaymentStatus.PAID, PaymentMethod.from(method), currency, totalAmount);
    }
}
