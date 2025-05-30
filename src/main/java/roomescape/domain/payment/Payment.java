package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@ToString
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String paymentKey;
    private String orderId;
    private String orderName;
    private long amount;

    private Payment(final Long id,
                    final String paymentKey,
                    final String orderId,
                    final String orderName,
                    final long amount) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.amount = amount;
    }

    protected Payment() {
    }

    public static Payment register(final String paymentKey, final String orderId, final String orderName,
                                   final long amount) {
        return new Payment(null, paymentKey, orderId, orderName, amount);
    }
}
