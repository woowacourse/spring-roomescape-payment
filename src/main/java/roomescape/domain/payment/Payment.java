package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider paymentProvider;

    @Column(nullable = false)
    private String providerPaymentId;

    @Column(nullable = false)
    private int amount;

    protected Payment() {
    }

    public Payment(PaymentProvider paymentProvider, String providerPaymentId, int amount) {
        this(null, paymentProvider, providerPaymentId, amount);
    }

    public Payment(Long id, PaymentProvider paymentProvider, String providerPaymentId, int amount) {
        this.id = id;
        this.paymentProvider = paymentProvider;
        this.providerPaymentId = providerPaymentId;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public String getProviderPaymentId() {
        return providerPaymentId;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
