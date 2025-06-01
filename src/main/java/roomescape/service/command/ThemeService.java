package roomescape.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Theme;
import roomescape.dto.business.ThemeCreationContent;
import roomescape.dto.response.ThemeResponse;
import roomescape.exception.BadRequestException;
import roomescape.repository.ThemeRepository;
import roomescape.service.query.ReservationQueryService;
import roomescape.service.query.ThemeQueryService;
import roomescape.service.query.WaitingQueryService;

@Service
@Transactional
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final ThemeQueryService themeQueryService;
    private final ReservationQueryService reservationQueryService;
    private final WaitingQueryService waitingQueryService;

    public ThemeService(ThemeRepository themeRepository, ThemeQueryService themeQueryService,
            ReservationQueryService reservationQueryService, WaitingQueryService waitingQueryService) {
        this.themeRepository = themeRepository;
        this.themeQueryService = themeQueryService;
        this.reservationQueryService = reservationQueryService;
        this.waitingQueryService = waitingQueryService;
    }

    public ThemeResponse addTheme(ThemeCreationContent request) {
        Theme theme = Theme.createWithoutId(request.name(), request.description(), request.thumbnail());
        Theme savedTheme = themeRepository.save(theme);
        return new ThemeResponse(savedTheme);
    }

    public void deleteThemeById(Long id) {
        Theme theme = themeQueryService.getThemeById(id);
        validateReservationInTheme(theme);
        validateMemberInTime(theme);
        themeRepository.deleteById(id);
    }

    private void validateReservationInTheme(Theme theme) {
        boolean isExistReservation = reservationQueryService.existsReservationInTheme(theme);
        if (isExistReservation) {
            throw new BadRequestException("이미 예약이 존재하는 테마입니다.");
        }
    }

    private void validateMemberInTime(Theme theme) {
        boolean isExistWaiting = waitingQueryService.existsWaitingInTheme(theme);
        if (isExistWaiting) {
            throw new BadRequestException("이미 예약이 존재하는 예약시간입니다.");
        }
    }
}
