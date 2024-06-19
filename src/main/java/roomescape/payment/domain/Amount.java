package roomescape.payment.domain;

import jakarta.persistence.Column;
import java.math.BigDecimal;
import java.util.Objects;
import roomescape.global.exception.IllegalRequestException;

public class Amount {
    @Column(name = "amount", nullable = false)
    private BigDecimal value;

    public Amount(BigDecimal value) {
        validate(value);
        this.value = value;
    }

    protected Amount() {
    }

    private void validate(BigDecimal value) {
        validateNotNull(value);
        validateNotNegative(value);
        validateNotDecimal(value);

    }

    public void validateNotNull(BigDecimal value) {
        if (value == null) {
            throw new IllegalRequestException("금액은 필수입니다.");
        }
    }

    public void validateNotNegative(BigDecimal value) {
        if (value.signum() == -1) {
            throw new IllegalRequestException("금액은 음수일 수 없습니다.");
        }
    }

    public void validateNotDecimal(BigDecimal value) {
        if (value.scale() > 0 && value.stripTrailingZeros().scale() > 0) {
            throw new IllegalRequestException("금액은 소수일 수 없습니다.");
        }
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Amount amount = (Amount) o;
        return Objects.equals(this.value, amount.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
