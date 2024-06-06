package roomescape.infrastructure.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.exception.theme.NotFoundThemeException;

@Repository
@RequiredArgsConstructor
public class ThemeRepositoryImpl implements ThemeRepository {
    private final ThemeJpaRepository themeJpaRepository;

    @Override
    public Theme save(Theme theme) {
        return themeJpaRepository.save(theme);
    }

    @Override
    public Theme getById(Long id) {
        return themeJpaRepository.findById(id).orElseThrow(NotFoundThemeException::new);
    }

    @Override
    public List<Theme> findPopularThemes(String startDate, String endDate, int limit) {
        return themeJpaRepository.findPopularThemes(startDate, endDate, limit);
    }

    @Override
    public List<Theme> findAll() {
        return themeJpaRepository.findAll();
    }

    @Override
    public void delete(Long themeId) {
        themeJpaRepository.deleteById(themeId);
    }
}
