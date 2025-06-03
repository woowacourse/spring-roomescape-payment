package roomescape.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.Theme;

import java.util.Optional;

public interface JpaThemeRepository extends JpaRepository<Theme, Long> {
    Optional<Theme> findByName(String name);
}
