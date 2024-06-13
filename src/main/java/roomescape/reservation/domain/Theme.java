package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.Objects;

import roomescape.exception.ErrorType;
import roomescape.exception.RoomescapeException;
import roomescape.payment.domain.PayAmount;

@Entity
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1023)
    private String description;

    private String thumbnail;

    @Embedded
    private PayAmount price;

    protected Theme() {
    }

    public Theme(Long id, String name, String description, String thumbnail, Long price) {
        validate(name, description, thumbnail);
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = new PayAmount(BigDecimal.valueOf(price));
    }

    public Theme(String name, String description, String thumbnail, Long price) {
        this(null, name, description, thumbnail, price);
    }

    private void validate(String name, String description, String thumbnail) {
        validateString(name);
        validateString(description);
        validateString(thumbnail);
    }

    private void validateString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new RoomescapeException(ErrorType.MISSING_REQUIRED_VALUE_ERROR);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public PayAmount getPayAmount() {
        return price;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Theme)) {
            return false;
        }
        Theme theme = (Theme) o;
        return Objects.equals(getId(), theme.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Theme{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", price=" + price +
                '}';
    }
}
