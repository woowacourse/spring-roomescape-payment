package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeName;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;

public interface ThemeRepository extends JpaRepository<Theme, Long> {

    default Theme findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new RoomEscapeException(
                ErrorCode.THEME_NOT_FOUND_BY_ID,
                "theme_id = " + id
        ));
    }

    boolean existsByThemeName(ThemeName name);
}
