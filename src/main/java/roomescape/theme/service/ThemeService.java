package roomescape.theme.service;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.custom.AlreadyInUseException;
import roomescape.common.exception.custom.EntityNotFoundException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeId;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;

@Slf4j
@Service
public class ThemeService {

    private static final int START_DATE_OFFSET = 8;
    private static final int END_DATE_OFFSET = 1;

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeService(final ThemeRepository themeRepository, final ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ThemeResponse create(final ThemeRequest request) {
        Theme theme = themeRepository.save(request.toEntity());
        log.info("테마 저장 완료: themeId={}", theme.getId());
        return ThemeResponse.from(theme);
    }

    public List<ThemeResponse> getAll() {
        return themeRepository.findAll()
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public List<ThemeResponse> getPopularThemes() {
        LocalDate now = LocalDate.now();

        LocalDate startDate = now.minusDays(START_DATE_OFFSET);
        LocalDate endDate = now.minusDays(END_DATE_OFFSET);
        int popularThemeCount = 10;

        return themeRepository.findTopByDateAndCount(startDate, endDate, popularThemeCount)
                .stream()
                .map(ThemeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(final Long id) {
        if (reservationRepository.existsByThemeId(new ThemeId(id))) {
            throw new AlreadyInUseException("예약 데이터가 있는 테마입니다.");
        }
        if (!themeRepository.existsById(new ThemeId(id))) {
            throw new EntityNotFoundException("존재하지 않는 테마입니다.");
        }
        themeRepository.deleteById(new ThemeId(id));
        log.info("테마 삭제 완료: themeId={}", id);
    }
}
