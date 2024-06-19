package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment")
public class Payment {

    private static final String ADMIN_PAYMENT_VALUE = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String paymentKey;

    private long amount;

    public static Payment byAdmin() {
        return new Payment(ADMIN_PAYMENT_VALUE,ADMIN_PAYMENT_VALUE,0);
    }

    protected Payment() {
    }

    public Payment(String orderId, String paymentKey, long amount) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public boolean isByAdmin() {
        return paymentKey.equals(ADMIN_PAYMENT_VALUE);
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public long getAmount() {
        return amount;
    }
}
