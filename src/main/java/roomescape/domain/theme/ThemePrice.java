package roomescape.domain.theme;

import jakarta.persistence.Embeddable;

@Embeddable
public class ThemePrice {
    private int price;

    protected ThemePrice() {
    }

    public ThemePrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
