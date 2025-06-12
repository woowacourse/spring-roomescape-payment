package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Accessors(fluent = true)
@ToString
@Entity
public class Payment {

    @Id
    @EqualsAndHashCode.Include
    private String orderId;
    private String paymentKey;
    private String orderName;
    private long amount;

    private Payment(final String orderId,
                    final String paymentKey,
                    final String orderName,
                    final long amount) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.amount = amount;
    }

    protected Payment() {
    }

    public static Payment register(final String orderId,
                                   final String paymentKey,
                                   final String orderName,
                                   final long amount) {
        return new Payment(orderId, paymentKey, orderName, amount);
    }
}
