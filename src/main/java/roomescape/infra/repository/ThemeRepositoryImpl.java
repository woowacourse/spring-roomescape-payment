package roomescape.infra.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;

@Repository
@RequiredArgsConstructor
public class ThemeRepositoryImpl implements ThemeRepository {
    private final ThemeJpaRepository themeJpaRepository;

    @Override
    public Theme save(Theme theme) {
        return themeJpaRepository.save(theme);
    }

    @Override
    public Optional<Theme> findTheme(Long id) {
        return themeJpaRepository.findById(id);
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
