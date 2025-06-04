package roomescape.payment.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Amount {

    private long amount;

    public Amount(long amount) {
        this.amount = amount;
    }

    protected Amount() {
    }

    public long getValue() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Amount amount1)) {
            return false;
        }
        return amount == amount1.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }
}
