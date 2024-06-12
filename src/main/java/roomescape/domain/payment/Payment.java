package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.exception.payment.PaymentAmountException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "requested_at", nullable = false)
    private String requestedAt;

    @Column(name = "approved_at", nullable = false)
    private String approvedAt;

    public Payment(BigDecimal amount, String paymentKey, String orderId, String requestedAt, String approvedAt) {
        this.amount = amount;
        validateAmount(amount);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.doubleValue() <= 0) {
            throw new PaymentAmountException();
        }
    }
}
