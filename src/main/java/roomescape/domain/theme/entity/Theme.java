package roomescape.domain.theme.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.theme.exception.ThemeException;

@Getter
@NoArgsConstructor
@Entity
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String thumbnail;

    public Theme(final Long id, final String name, final String description, final String thumbnail) {
        validateName(name);
        validateDescription(description);
        validateThumbnail(thumbnail);
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public Theme(final String name, final String description, final String thumbnail) {
        this(null, name, description, thumbnail);
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new ThemeException("테마 이름은 NULL, 공백이 허용되지 않습니다.");
        }
    }

    private void validateDescription(final String description) {
        if (description == null || description.isBlank()) {
            throw new ThemeException("테마 설명은 NULL, 공백이 허용되지 않습니다.");
        }
    }

    private void validateThumbnail(final String thumbnail) {
        if (thumbnail == null || thumbnail.isBlank()) {
            throw new ThemeException("썸네일은 NULL, 공백이 허용되지 않습니다.");
        }
    }
}
