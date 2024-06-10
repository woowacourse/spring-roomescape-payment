package roomescape.payment.dto;

import java.math.BigDecimal;

import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.entity.Payment;

public record PaymentResponse(String orderName, String paymentKey, BigDecimal totalAmount, String approvedAt) {
    public static PaymentResponse from(PaymentInfo paymentInfo) {
        return new PaymentResponse(paymentInfo.orderName(), paymentInfo.paymentKey(), paymentInfo.totalAmount(), paymentInfo.approvedAt());
    }

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getOrderName(), payment.getPaymentKey(), payment.getAmount(), payment.getApprovedAt());
    }
}
