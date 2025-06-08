package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.payment.exception.OrderIdRequiredException;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class OrderId {
    public static final int MAX_LENGTH = 64;
    public static final int MIN_LENGTH = 6;

    @Column(name = "order_id", nullable = false, unique = true)
    private String value;

    public OrderId(String value) {
        validateOrderId(value);
        this.value = value;
    }

    private static void validateOrderId(String id) {
        if (id.length() < MIN_LENGTH || id.length() > MAX_LENGTH) {
            throw new OrderIdRequiredException();
        }
    }
}
