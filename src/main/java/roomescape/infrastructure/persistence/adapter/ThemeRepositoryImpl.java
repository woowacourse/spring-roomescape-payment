package roomescape.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.infrastructure.persistence.jpa.ThemeJpaRepository;

@Repository
@RequiredArgsConstructor
public class ThemeRepositoryImpl implements ThemeRepository {

    private final ThemeJpaRepository themeJpaRepository;

    @Override
    public Theme save(final Theme theme) {
        return themeJpaRepository.save(theme);
    }

    @Override
    public void deleteById(final Long id) {
        themeJpaRepository.deleteById(id);
    }

    @Override
    public List<Theme> findAll() {
        return themeJpaRepository.findAll();
    }

    @Override
    public List<Theme> findAllPopularThemes(final LocalDate from, final LocalDate to) {
        return themeJpaRepository.findAllPopularThemes(from, to);
    }

    @Override
    public Optional<Theme> findById(final Long id) {
        return themeJpaRepository.findById(id);
    }
}
