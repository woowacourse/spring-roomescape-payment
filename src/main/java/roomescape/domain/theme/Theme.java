package roomescape.domain.theme;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import roomescape.exception.RoomescapeException;

@Entity
public class Theme {
    private static final int MAX_DESCRIPTION_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private ThemeName name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String thumbnail;
    @Column(nullable = false)
    private BigDecimal price;

    protected Theme() {
    }

    public Theme(ThemeName name, String description, String thumbnail, BigDecimal price) {
        this(null, name, description, thumbnail, price);
    }

    public Theme(Long id, ThemeName name, String description, String thumbnail, BigDecimal price) {
        int descriptionLength = description.length();
        if (descriptionLength > MAX_DESCRIPTION_LENGTH) {
            throw new RoomescapeException(String.format("테마 설명은 %s자 이하만 가능합니다.", MAX_DESCRIPTION_LENGTH));
        }
        if (thumbnail == null || thumbnail.isBlank()) {
            throw new RoomescapeException("테마 썸네일은 비어있을 수 없습니다.");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.asText();
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public BigDecimal getPrice() {
        return price.setScale(0, RoundingMode.FLOOR);
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
