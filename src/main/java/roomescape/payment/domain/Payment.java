package roomescape.payment.domain;

import jakarta.persistence.Embedded;
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

    @Embedded
    private PaymentInfo paymentInfo;

    @Enumerated(value = EnumType.STRING)
    private PaymentGateway paymentGateway;

    protected Payment() {
    }

    public Payment(final Long id, final PaymentInfo paymentInfo, final PaymentGateway paymentGateway) {
        this.id = id;
        this.paymentInfo = paymentInfo;
        this.paymentGateway = paymentGateway;
    }

    public Payment(final PaymentInfo paymentInfo, final PaymentGateway paymentGateway) {
        this(null, paymentInfo, paymentGateway);
    }

    public Long getId() {
        return id;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment payment = (Payment) o;
        if (id == null || payment.id == null) {
            return false;
        }
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
