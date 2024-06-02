package roomescape.domain.reservationdetail;

import java.util.List;

public interface ThemeRepository {
    Theme save(Theme theme);

    Theme getById(Long id);

    List<Theme> findPopularThemes(String startDate, String endDate, int limit);

    List<Theme> findAll();

    void delete(Long themeId);
}
