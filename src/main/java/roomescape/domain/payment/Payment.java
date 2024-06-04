package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "payment")
public class Payment {
    private static final String ADMIN_MARKER = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String paymentKey;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    protected Payment() {
    }

    public Payment(String orderId, String paymentKey, BigDecimal amount, PaymentType paymentType) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public Payment(String orderId, String paymentKey, BigDecimal amount, String paymentType) {
        this(orderId, paymentKey, amount, PaymentType.valueOf(paymentType));
    }

    public static Payment ofAdmin() {
        return new Payment(ADMIN_MARKER, ADMIN_MARKER, BigDecimal.ZERO, PaymentType.ADMIN);
    }

    public boolean isByAdmin() {
        return paymentType.isByAdmin();
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

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
}
