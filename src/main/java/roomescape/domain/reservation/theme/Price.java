package roomescape.domain.reservation.theme;

import jakarta.persistence.Embeddable;

@Embeddable
public class Price {

    private Long price;

    public Price() {
    }

    public Price(Long price) {
        validate(price);
        this.price = price;
    }

    private void validate(Long price) {
        if (price == null) {
            throw new IllegalArgumentException("가격이 비어 있습니다.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("가격은 음수가 될 수 없습니다.");
        }
    }

    public Long getPrice() {
        return price;
    }

    public boolean isPriceEquals(Long price) {
        return this.price.equals(price);
    }
}
