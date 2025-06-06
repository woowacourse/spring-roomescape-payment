package roomescape.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import roomescape.order.Order;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    private String paymentKey;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    protected Payment() {
    }

    public Payment(Long id, Long amount, String paymentKey, Order order) {
        this.id = id;
        this.amount = amount;
        this.paymentKey = paymentKey;
        this.order = order;
    }

    public static Payment create(Long amount, String paymentKey, Order order) {
        return new Payment(null, amount, paymentKey, order);
    }
}
