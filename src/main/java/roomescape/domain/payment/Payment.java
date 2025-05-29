package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import roomescape.infrastructure.error.exception.PaymentException;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private long amount;

    public Payment(String orderId, long amount) {
        this(null, orderId, amount);
    }

    public Payment(Long id, String orderId, long amount) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
    }

    protected Payment() {
    }

    public void validateApprovalAmount(long approvalAmount) {
        if (this.amount != approvalAmount) {
            throw new PaymentException(
                    "결제 금액(%,d)과 승인 요청 금액(%,d)이 일치하지 않아 결제 승인을 거부합니다.".formatted(this.amount, approvalAmount)
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
