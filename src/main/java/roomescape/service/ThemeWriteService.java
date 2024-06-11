package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.repository.ThemeRepository;

@Transactional
@Service
public class ThemeWriteService {

    private final ThemeRepository themeRepository;

    public ThemeWriteService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public void deleteTheme(long id) {
        themeRepository.deleteById(id);
    }
}
