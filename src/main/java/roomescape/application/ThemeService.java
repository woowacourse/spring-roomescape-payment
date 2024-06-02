package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import roomescape.application.dto.request.theme.ThemeRequest;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.application.policy.RankingPolicy;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.exception.theme.ReservationReferencedThemeException;

@Service
@RequiredArgsConstructor
public class ThemeService {
    private final ThemeRepository themeRepository;

    public ThemeResponse saveTheme(ThemeRequest request) {
        Theme savedTheme = themeRepository.save(request.toTheme());
        return ThemeResponse.from(savedTheme);
    }

    public List<ThemeResponse> findAllTheme() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<ThemeResponse> findAllPopularThemes(RankingPolicy rankingPolicy) {
        LocalDate startDate = rankingPolicy.getStartDateAsString();
        LocalDate endDate = rankingPolicy.getEndDateAsString();
        int limit = rankingPolicy.exposureSize();

        List<Theme> themes = themeRepository.findPopularThemes(startDate.toString(), endDate.toString(), limit);

        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public void deleteTheme(Long id) {
        try {
            themeRepository.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new ReservationReferencedThemeException();
        }
    }
}
