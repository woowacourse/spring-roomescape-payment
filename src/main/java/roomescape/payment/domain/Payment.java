package roomescape.payment.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import roomescape.payment.domain.vo.Amount;
import roomescape.payment.domain.vo.PaymentStatus;

@Entity
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    private String orderId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount", nullable = false))
    private Amount amount;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus status;

    protected Payment() {}

    public Payment(Long id, String paymentKey, String orderId, Long amount, PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = new Amount(amount);
        this.status = status;
    }

    public Payment(String paymentKey, String orderId, Long amount, PaymentStatus status) {
        this(null, paymentKey, orderId, amount, status);
    }

    public void complete() {
        status = PaymentStatus.COMPLETE;
    }

    public void fail() {
        status = PaymentStatus.FAILED;
    }

    public Long getAmount() {
        return amount.value();
    }
}
