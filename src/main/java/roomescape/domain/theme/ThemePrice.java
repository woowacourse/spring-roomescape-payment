package roomescape.domain.theme;

import jakarta.persistence.Embeddable;
import roomescape.exception.theme.InvalidThemePriceRangeException;

@Embeddable
public record ThemePrice(int price) {
    private static final int MIN_PRICE = 0;

    public ThemePrice {
        validatePriceRange(price);
    }

    private void validatePriceRange(int price) {
        if (price < MIN_PRICE) {
            throw new InvalidThemePriceRangeException();
        }
    }
}
