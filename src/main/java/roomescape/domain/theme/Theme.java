package roomescape.domain.theme;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private ThemeName name;
    private String description;
    private String thumbnail;
    @Embedded
    private ThemePrice price;

    protected Theme() {
    }

    public Theme(Long id) {
        this(id, null, null, null, 0);
    }

    public Theme(ThemeName name, String description, String thumbnail, int price) {
        this(null, name, description, thumbnail, price);
    }

    public Theme(Long id, ThemeName name, String description, String thumbnail, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = new ThemePrice(price);
    }

    public Long getId() {
        return id;
    }

    public ThemeName getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getPrice() {
        return price.price();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Theme theme = (Theme) o;
        return Objects.equals(id, theme.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
