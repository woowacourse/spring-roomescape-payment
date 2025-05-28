package roomescape.theme.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Theme {

    private static final Duration THEME_USAGE_DURATION = Duration.ofHours(2);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String thumbnail;
    private BigDecimal price;

    public Theme(final Long id, final String name, final String description, final String thumbnail,
                 final BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = price;
    }

    public Theme(final String name, final String description, final String thumbnail, final BigDecimal price) {
        this(null, name, description, thumbnail, price);
    }

    public Duration getDuration() {
        return THEME_USAGE_DURATION;
    }
}
