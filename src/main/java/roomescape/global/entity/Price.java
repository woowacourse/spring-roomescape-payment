package roomescape.global.entity;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Price {

    private static final BigDecimal MIN_PRICE = BigDecimal.ZERO;

    private static final BigDecimal MAX_PRICE = BigDecimal.valueOf(2_100_000_000);

    private BigDecimal price;

    public Price(BigDecimal price) {
        validate(price);
        this.price = price;
    }

    protected Price() {
    }

    private void validate(BigDecimal price) {
        if (price == null || isOutOfRange(price)) {
            throw new IllegalArgumentException("금액은 null이거나 0보다 작을 수 없습니다.");
        }
    }

    private static boolean isOutOfRange(BigDecimal price) {
        return price.compareTo(MIN_PRICE) < 0 || price.compareTo(MAX_PRICE) > 0;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Price price1 = (Price) o;
        return Objects.equals(price, price1.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }
}
