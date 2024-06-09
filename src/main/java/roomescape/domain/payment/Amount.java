package roomescape.domain.payment;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Amount {

    private BigDecimal amount;

    protected Amount() {
    }

    public Amount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
