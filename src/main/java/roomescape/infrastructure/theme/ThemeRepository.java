package roomescape.infrastructure.theme;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.theme.repository.ThemeRepositoryInterface;
import roomescape.infrastructure.exception.DataNotFoundException;

@RequiredArgsConstructor
@Repository
public class ThemeRepository implements ThemeRepositoryInterface {

    private final JpaThemeRepository jpaThemeRepository;

    @Override
    public boolean existsByName(final String name) {
        return jpaThemeRepository.existsByName(name);
    }

    @Override
    public List<Theme> findAll() {
        return jpaThemeRepository.findAll();
    }

    @Override
    public Theme findById(final Long id) {
        return jpaThemeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당 테마 데이터가 존재하지 않습니다. id = " + id));
    }

    @Override
    public Theme save(final Theme theme) {
        return jpaThemeRepository.save(theme);
    }

    @Override
    public void deleteById(final Long id) {
        jpaThemeRepository.deleteById(id);
    }
}
