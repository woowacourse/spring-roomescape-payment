package roomescape.theme.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import roomescape.global.exception.BadRequestException;

@Embeddable
@Slf4j
public record ThemeName(
        @Column(nullable = false)
        @Size(max = ThemeName.MAX_NAME_LENGTH)
        String name
) {
    private static final int MAX_NAME_LENGTH = 5;

    public ThemeName(final String name) {
        this.name = Objects.requireNonNull(name, "name은 null일 수 없습니다.");
        if (name.isBlank()) {
            log.warn("[VALIDATION-FAIL] 테마 이름 공백");
            throw new BadRequestException("name은 공백일 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            log.warn("[VALIDATION-FAIL] 테마 이름 길이 초과");
            throw new BadRequestException("name은 " + MAX_NAME_LENGTH + "자 이내여야 합니다.");
        }
    }
}
