package roomescape.payment.dto;

import roomescape.payment.domain.Orders;

public record PaymentResponse(String orderId, String paymentKey) {

    public static PaymentResponse from(Orders orders) {
        return new PaymentResponse(orders.getOrderId(), orders.getPaymentKey());
    }
}
