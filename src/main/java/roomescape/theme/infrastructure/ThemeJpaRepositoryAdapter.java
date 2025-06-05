package roomescape.theme.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;

@Repository
public class ThemeJpaRepositoryAdapter implements ThemeRepository {

    private final ThemeJpaRepository themeJpaRepository;

    public ThemeJpaRepositoryAdapter(final ThemeJpaRepository themeJpaRepository) {
        this.themeJpaRepository = themeJpaRepository;
    }

    @Override
    public Theme save(Theme theme) {
        return themeJpaRepository.save(theme);
    }

    @Override
    public Optional<Theme> findById(Long id) {
        return themeJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        themeJpaRepository.deleteById(id);
    }

    @Override
    public List<Theme> findPopularThemes(LocalDate start, LocalDate end) {
        return themeJpaRepository.findPopularThemes(start, end);
    }

    @Override
    public List<Theme> findAll() {
        return themeJpaRepository.findAll();
    }
}
