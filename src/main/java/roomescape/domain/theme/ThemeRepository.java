package roomescape.domain.theme;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ThemeRepository {

    Theme save(Theme theme);

    void deleteById(Long id);

    List<Theme> findAll();

    List<Theme> findAllPopularThemes(LocalDate from, LocalDate to);

    Optional<Theme> findById(Long id);
}
