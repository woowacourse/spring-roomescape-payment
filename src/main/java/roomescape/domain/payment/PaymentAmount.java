package roomescape.domain.payment;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;
import roomescape.exception.payment.PaymentAmountException;

@Embeddable
public class PaymentAmount {

    private BigDecimal amount;

    public PaymentAmount(BigDecimal amount) {
        validateAmount(amount);
        this.amount = amount;
    }

    protected PaymentAmount() {
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.doubleValue() <= 0) {
            throw new PaymentAmountException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentAmount that = (PaymentAmount) o;
        return Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }

    public BigDecimal value() {
        return amount;
    }
}
