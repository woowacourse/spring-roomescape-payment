package roomescape.domain.reservationdetail;

import java.util.List;
import java.util.Optional;
import roomescape.exception.RoomEscapeException;

public interface ThemeRepository {
    Theme save(Theme theme);

    default Theme getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new RoomEscapeException("존재하지 않는 테마입니다."));
    }

    Optional<Theme> findById(Long id);

    List<Theme> findPopularThemes(String startDate, String endDate, int limit);

    List<Theme> findAll();

    void deleteById(Long themeId);
}
