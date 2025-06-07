package roomescape.theme.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import roomescape.theme.exception.ThemeException;

@Entity
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String thumbnail;

    protected Theme() {
    }

    private Theme(final Long id, final String name, final String description, final String thumbnail) {
        validateIsNonNull(name);
        validateIsNonNull(description);
        validateIsNonNull(thumbnail);

        validateIsBlank(name);
        validateIsBlank(description);
        validateIsBlank(thumbnail);

        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public static Theme createWithoutId(final String name, final String description, final String thumbnail) {
        return new Theme(null, name, description, thumbnail);
    }

    private void validateIsNonNull(final Object object) {
        if (object == null) {
            throw new ThemeException("테마 정보는 필수입니다.");
        }
    }

    private void validateIsBlank(final String something) {
        if (something.isBlank()) {
            throw new ThemeException("테마 정보는 비어있을 수 없습니다.");
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

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Theme that)) {
            return false;
        }

        if (getId() == null && that.getId() == null) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
