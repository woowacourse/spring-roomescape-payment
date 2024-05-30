package roomescape.domain.reservationdetail;

import java.util.List;
import java.util.Optional;
import roomescape.exception.theme.NotFoundThemeException;

public interface ThemeRepository {
    Theme save(Theme theme);

    default Theme getById(Long id) {
        return findTheme(id)
                .orElseThrow(NotFoundThemeException::new);
    }

    Optional<Theme> findTheme(Long id);

    List<Theme> findPopularThemes(String startDate, String endDate, int limit);

    List<Theme> findAll();

    void delete(Long themeId);
}
