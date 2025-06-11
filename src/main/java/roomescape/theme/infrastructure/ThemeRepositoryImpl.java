package roomescape.theme.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.ReservationPeriod;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ThemeRepositoryImpl implements ThemeRepository {
    private final JpaThemeRepository jpaThemeRepository;

    @Override
    public Theme save(Theme theme) {
        return jpaThemeRepository.save(theme);
    }

    @Override
    public List<Theme> findAll() {
        return jpaThemeRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaThemeRepository.deleteById(id);
    }

    @Override
    public Optional<Theme> findById(Long id) {
        return jpaThemeRepository.findById(id);
    }

    @Override
    public List<Theme> findPopularThemes(ReservationPeriod period, int popularCount) {
        return jpaThemeRepository.findPopularThemes(period, popularCount);
    }
}
