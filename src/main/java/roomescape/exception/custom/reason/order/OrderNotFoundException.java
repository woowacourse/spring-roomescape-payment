package roomescape.exception.custom.reason.order;

import roomescape.exception.custom.status.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException() {
        super("주문이 존재하지 않습니다.");
    }
}
