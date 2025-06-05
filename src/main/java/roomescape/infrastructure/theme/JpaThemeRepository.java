package roomescape.infrastructure.theme;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import roomescape.domain.theme.entity.Theme;

public interface JpaThemeRepository extends CrudRepository<Theme, Long> {

    boolean existsByName(String name);

    List<Theme> findAll();
}
