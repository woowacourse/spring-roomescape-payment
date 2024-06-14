package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.model.Theme;
import roomescape.repository.ThemeRepository;
import roomescape.request.ThemeRequest;

import java.time.LocalDate;
import java.util.List;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(final ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<Theme> findAllThemes() {
        return themeRepository.findAll();
    }

    public Theme addTheme(final ThemeRequest themeRequest) {
        Theme theme = new Theme(themeRequest.name(), themeRequest.description(), themeRequest.thumbnail());
        return themeRepository.save(theme);
    }

    public void deleteTheme(final Long id) {
        themeRepository.deleteById(id);
    }

    public List<Theme> findPopularThemes() {
        LocalDate before = LocalDate.now().minusDays(8);
        LocalDate after = LocalDate.now().minusDays(1);
        return themeRepository.findFirst10ByDateBetweenOrderByTheme(before, after);
    }

}
