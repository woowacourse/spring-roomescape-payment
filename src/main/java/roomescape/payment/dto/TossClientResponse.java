package roomescape.payment.dto;

import java.math.BigDecimal;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;

public record TossClientResponse(String paymentKey, String orderId, BigDecimal totalAmount, String status) {

    public Payment toPayment(){
        return new Payment(paymentKey, orderId, totalAmount, PaymentStatus.fromTossPayStatus(status));
    }
}
