package roomescape.domain.theme;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.exception.BusinessRuleViolationException;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
public class Theme {

    private static final int NAME_MAX_LENGTH = 10;
    private static final int DESCRIPTION_MAX_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String thumbnail;

    private Theme(final Long id,
                  final String name,
                  final String description,
                  final String thumbnail) {

        validateName(name);
        validateDescription(description);
        validateThumbnail(thumbnail);

        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public static Theme ofExisting(final long id,
                                   final String name,
                                   final String description,
                                   final String thumbnail) {
        return new Theme(id, name, description, thumbnail);
    }

    public static Theme register(final String name, final String description, final String thumbnail) {
        return new Theme(null, name, description, thumbnail);
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleViolationException("테마 이름은 null이거나 공백일 수 없습니다.");

        }
        if (name.length() > NAME_MAX_LENGTH) {
            throw new BusinessRuleViolationException(String.format("이름은 %d자를 넘길 수 없습니다.", NAME_MAX_LENGTH));
        }
    }

    private void validateDescription(final String description) {
        if (description == null || description.isBlank()) {
            throw new BusinessRuleViolationException("테마 설명은 null이거나 공백일 수 없습니다.");
        }
        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new BusinessRuleViolationException(String.format("설명은 %d자를 넘길 수 없습니다.", DESCRIPTION_MAX_LENGTH));
        }
    }

    private void validateThumbnail(final String thumbnail) {
        if (thumbnail == null || thumbnail.isBlank()) {
            throw new BusinessRuleViolationException("썸네일은 null이거나 공백일 수 없습니다.");
        }
    }
}
