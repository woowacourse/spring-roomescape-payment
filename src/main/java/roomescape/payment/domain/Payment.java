package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private LocalDateTime paymentDateTime;

    private Long amount;

    private PaymentStatus status;

    private String paymentKey;

    public Payment(final String orderId, final LocalDateTime paymentDateTime, final Long amount, final PaymentStatus status, final String paymentKey) {
        this.orderId = orderId;
        this.paymentDateTime = paymentDateTime;
        this.amount = amount;
        this.status = status;
        this.paymentKey = paymentKey;
    }

    public Payment() {
    }

    public void cancel() {
        this.status = PaymentStatus.CANCEL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment payment)) return false;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDateTime getPaymentDateTime() {
        return paymentDateTime;
    }

    public Long getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
