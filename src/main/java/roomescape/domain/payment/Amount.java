package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class Amount {

    private static final int MIN_AMOUNT = 0;

    @Column(nullable = false)
    private BigDecimal amount;

    protected Amount() {
    }

    public Amount(BigDecimal amount) {
        validateAmount(amount);
        this.amount = amount;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < MIN_AMOUNT) {
            throw new IllegalArgumentException(String.format("금액은 %d 이상이어야 합니다.", MIN_AMOUNT));
        }
    }

    protected BigDecimal getValue() {
        return amount;
    }
}
