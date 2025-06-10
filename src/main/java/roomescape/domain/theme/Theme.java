package roomescape.domain.theme;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import roomescape.exception.theme.ThemeException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
@EqualsAndHashCode(of = "id")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String thumbnail;

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
}
