package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.theme.ThemeRequest;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.application.policy.RankingPolicy;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.exception.RoomEscapeException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeService {
    private final ThemeRepository themeRepository;

    @Transactional
    public ThemeResponse saveTheme(ThemeRequest request) {
        Theme theme = request.toTheme();
        Theme savedTheme = themeRepository.save(theme);
        return ThemeResponse.from(savedTheme);
    }

    public List<ThemeResponse> findAllTheme() {
        List<Theme> themes = themeRepository.findAll();
        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<ThemeResponse> findAllPopularThemes(RankingPolicy rankingPolicy) {
        LocalDate startDate = rankingPolicy.getStartDateAsString();
        LocalDate endDate = rankingPolicy.getEndDateAsString();
        int limit = rankingPolicy.exposureSize();

        List<Theme> themes = themeRepository.findPopularThemes(
                startDate.toString(), endDate.toString(), limit);

        return themes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional
    public void deleteTheme(Long id) {
        try {
            themeRepository.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new RoomEscapeException("예약이 존재하는 테마입니다.");
        }
    }
}
