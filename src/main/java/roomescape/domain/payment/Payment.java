package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import roomescape.infrastructure.error.exception.PaymentException;

import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private long amount;

    public Payment(final String orderId, final Long amount) {
        this(null, orderId, amount);
    }

    public Payment(final Long id, final String orderId, final Long amount) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
    }

    protected Payment() {
    }

    public void validateApprovalAmount(final long amount) {
        if (this.amount != amount) {
            throw new PaymentException(
                    "요청 금액과 승인 금액이 일치하지 않습니다. 현재 결제 금액: %d, 요청 금액: %d".formatted(this.amount, amount)
            );
        }
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
