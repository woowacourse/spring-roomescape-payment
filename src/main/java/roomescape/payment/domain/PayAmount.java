package roomescape.payment.domain;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

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
}
