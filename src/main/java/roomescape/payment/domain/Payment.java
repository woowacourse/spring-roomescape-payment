package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    private String paymentKey;

    @Column(nullable = false, unique = true, name = "orderId")
    private String orderId;

    @Column(nullable = false, name = "amount")
    private BigDecimal amount;

    @Embedded
    private PaymentProduct paymentProduct;

    protected Payment() {
    }

    public Payment(String paymentKey, String orderId, BigDecimal amount, PaymentProduct paymentProduct) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentProduct = paymentProduct;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getPaymentProductId() {
        return paymentProduct.getProductId();
    }
}
