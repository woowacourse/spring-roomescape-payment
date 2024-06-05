package roomescape.domain.theme;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.exception.theme.NotFoundThemeException;

public interface ThemeRepository extends JpaRepository<Theme, Long> {

    default Theme getThemeById(long id) {
        return findById(id)
                .orElseThrow(NotFoundThemeException::new);
    }
}
