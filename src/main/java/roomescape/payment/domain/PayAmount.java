package roomescape.payment.domain;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class PayAmount {

    private BigDecimal amount;

    public PayAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public static PayAmount from(Long amount) {
        return new PayAmount(new BigDecimal(amount));
    }

    protected PayAmount() {
    }

    public BigDecimal getAmount() {
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
        PayAmount payAmount = (PayAmount) o;
        return Objects.equals(getAmount(), payAmount.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAmount());
    }
}
