package roomescape.payment.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class OrderId {

    private String orderId;

    public OrderId(final String orderId) {
        this.orderId = orderId;
    }

    protected OrderId() {
    }

    public String getValue() {
        return orderId;
    }

    @Override
    public final boolean equals(final Object o) {
        if (!(o instanceof final OrderId orderId1)) {
            return false;
        }

        return Objects.equals(orderId, orderId1.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderId);
    }
}
