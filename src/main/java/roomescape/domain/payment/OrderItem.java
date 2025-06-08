package roomescape.domain.payment;

import java.util.Arrays;
import roomescape.exception.common.NotFoundException;

public enum OrderItem {
    RESERVATION("RESERVATION", 1000L),
    ;

    private final String name;
    private final Long price;

    OrderItem(String name, Long price) {
        this.name = name;
        this.price = price;
    }

    public static OrderItem findByOrderIdPrefix(String orderId) {
        return Arrays.stream(OrderItem.values())
                .filter(item -> orderId.startsWith(item.name))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("결제 상품 정보를 찾을 수 없습니다."));
    }

    public boolean isSameAmount(Long orderAmount, Long orderQuantity) {
        return orderAmount.equals(price * orderQuantity);
    }
}
