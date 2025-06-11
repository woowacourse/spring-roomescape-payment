package roomescape.exception.custom.reason.order;

import lombok.Getter;

@Getter
public class OrderNotMatchException extends IllegalArgumentException {

    private final String orderId;

    public OrderNotMatchException(final String message, final String orderId) {
        super(message);
        this.orderId = orderId;
    }
}
