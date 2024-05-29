package roomescape.global.entity;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Price {

    private BigDecimal price;

    public Price(BigDecimal price) {
        this.price = price;
    }

    protected Price() {
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price1 = (Price) o;
        return Objects.equals(price, price1.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }
}
