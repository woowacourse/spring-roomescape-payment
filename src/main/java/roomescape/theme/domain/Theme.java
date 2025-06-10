package roomescape.theme.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor
public class Theme {

    private static int MAX_NAME = 255;
    private static int MAX_DESCRIPTION = 255;
    private static int MAX_THUMBNAIL = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String thumbnail;

    private Theme(final Long id, final String name, final String description, final String thumbnail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public static Theme createWithoutId(final String name, final String description, final String thumbnail) {
        validate(name, description, thumbnail);
        return new Theme(null, name, description, thumbnail);
    }

    private static void validate(final String name, final String description, final String thumbnail) {
        if (name == null || name.isBlank() || name.length() > MAX_NAME) {
            throw new IllegalArgumentException("이름은 1글자 이상, 255글자 이하여야합니다.");
        }
        if (description == null || description.isBlank() || description.length() > MAX_DESCRIPTION) {
            throw new IllegalArgumentException("설명은 1글자 이상, 255글자 이하여야합니다.");
        }
        if (thumbnail == null || thumbnail.isBlank() || thumbnail.length() > MAX_THUMBNAIL) {
            throw new IllegalArgumentException("썸네일 URI는 1글자 이상, 255글자 이하여야합니다.");
        }
    }
}
