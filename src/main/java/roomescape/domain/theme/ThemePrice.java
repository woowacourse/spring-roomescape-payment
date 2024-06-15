package roomescape.domain.theme;

import jakarta.persistence.Embeddable;
import roomescape.exception.theme.InvalidThemePriceRangeException;

@Embeddable
public class ThemePrice {
    private int price;

    protected ThemePrice() {
    }

    public ThemePrice(int price) {
        this.price = price;
        validatePriceRange(price);
    }

    private void validatePriceRange(int price) {
        if (price < 0) {
            throw new InvalidThemePriceRangeException();
        }
    }

    public int getPrice() {
        return price;
    }
}
