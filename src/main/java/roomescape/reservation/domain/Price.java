package roomescape.reservation.domain;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class Price {

    private BigDecimal price;

    public Price(Long price) {
        this.price = new BigDecimal(price);
    }

    public Price() {
    }

    public BigDecimal getPrice() {
        return price;
    }
}
