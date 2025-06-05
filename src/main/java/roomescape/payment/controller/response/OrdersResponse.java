package roomescape.payment.controller.response;

import roomescape.payment.domain.Orders;

public record OrdersResponse(String orderId) {

    public static OrdersResponse from(Orders orders) {
        if (orders == null) {
            return new OrdersResponse(null);
        }
        return new OrdersResponse(orders.getOrderId());
    }
}
