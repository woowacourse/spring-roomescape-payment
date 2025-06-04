package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private LocalDateTime paymentDateTime;

    private Long amount;

    private PaymentStatus status;

    public Payment(final String orderId, final LocalDateTime paymentDateTime, final Long amount, final PaymentStatus status) {
        this.orderId = orderId;
        this.paymentDateTime = paymentDateTime;
        this.amount = amount;
        this.status = status;
    }

    public Payment() {
    }

    public void cancel() {
        this.status = PaymentStatus.CANCEL;
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
}
