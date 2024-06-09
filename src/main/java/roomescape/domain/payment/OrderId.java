package roomescape.domain.payment;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrderId {

    private String orderId;

    protected OrderId() {
    }

    public OrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
