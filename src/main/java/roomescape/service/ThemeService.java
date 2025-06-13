package roomescape.service;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ThemeRanking;
import roomescape.dto.request.CreateThemeRequest;
import roomescape.entity.Reservation;
import roomescape.entity.Theme;
import roomescape.exception.custom.InvalidThemeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;

@Service
@Transactional
@Slf4j
public class ThemeService {

    private static final int THEME_RANKING_END_RANGE = 7;
    private static final int THEME_RANKING_START_RANGE = 1;

    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;

    public ThemeService(ReservationRepository reservationRepository, ThemeRepository themeRepository) {
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
    }

    public Theme addTheme(CreateThemeRequest request) {
        log.info("테마 생성 시작 - name: {}, description: {}, thumbnail: {}",
                request.name(), request.description(), request.thumbnail());

        Theme theme = request.toTheme();
        boolean existsByName = themeRepository.existsByName(theme.getName());
        if (existsByName) {
            log.error("중복된 테마 이름 - name: {}", request.name());
            throw new InvalidThemeException("중복된 테마 이름입니다.");
        }

        Theme savedTheme = themeRepository.save(theme);
        log.info("테마 생성 완료 - themeId: {}, name: {}", savedTheme.getId(), savedTheme.getName());
        return savedTheme;
    }

    public List<Theme> findAll() {
        log.info("테마 목록 조회 시작");
        List<Theme> themes = themeRepository.findAll();
        log.info("테마 목록 조회 완료 - 총 {}개", themes.size());
        return themes;
    }

    public void deleteThemeById(long id) {
        log.info("테마 삭제 시작 - themeId: {}", id);

        if (reservationRepository.existsByThemeId(id)) {
            log.error("예약이 존재하는 테마 삭제 시도 - themeId: {}", id);
            throw new InvalidThemeException("예약이 존재하는 테마는 삭제할 수 없습니다.");
        }

        themeRepository.deleteById(id);
        log.info("테마 삭제 완료 - themeId: {}", id);
    }

    public List<Theme> getRankingThemes(LocalDate originDate) {
        log.info("테마 랭킹 조회 시작 - originDate: {}", originDate);

        LocalDate end = originDate.minusDays(THEME_RANKING_START_RANGE);
        LocalDate start = originDate.minusDays(THEME_RANKING_END_RANGE);

        List<Reservation> inRangeReservations = reservationRepository.findAllByDateBetween(start, end);

        ThemeRanking themeRanking = new ThemeRanking(inRangeReservations);
        List<Theme> rankings = themeRanking.getAscendingRanking();

        log.info("테마 랭킹 조회 완료 - 총 {}개", rankings.size());
        return rankings;
    }
}
