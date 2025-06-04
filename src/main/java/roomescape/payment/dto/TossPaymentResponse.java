package roomescape.payment.dto;

import roomescape.payment.domain.Orders;

public record TossPaymentResponse(String orderId, String paymentKey) {

    public static TossPaymentResponse from(Orders orders) {
        return new TossPaymentResponse(orders.getOrderId(), orders.getPaymentKey());
    }
}
