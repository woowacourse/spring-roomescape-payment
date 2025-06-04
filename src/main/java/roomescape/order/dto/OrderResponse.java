package roomescape.order.dto;

import roomescape.order.Order;

public record OrderResponse(
        String paymentKey,
        Long amount
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(order.getPaymentKey(), order.getAmount());
    }

    public static OrderResponse createEmptyOrderResponse() {
        return new OrderResponse("", 0L);
    }
}
