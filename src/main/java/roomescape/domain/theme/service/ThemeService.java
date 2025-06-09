package roomescape.domain.theme.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.theme.domain.Theme;
import roomescape.domain.theme.dto.ThemeCreationContent;
import roomescape.domain.theme.repository.ThemeRepository;
import roomescape.domain.theme.response.AddThemeResponse;
import roomescape.exception.BadRequestException;
import roomescape.domain.reservation.service.ReservationQueryService;
import roomescape.domain.waiting.service.WaitingQueryService;

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

    public AddThemeResponse addTheme(ThemeCreationContent request) {
        Theme theme = Theme.createWithoutId(request.name(), request.description(), request.thumbnail());
        Theme savedTheme = themeRepository.save(theme);
        return new AddThemeResponse(savedTheme);
    }

    public void deleteThemeById(Long id) {
        Theme theme = themeQueryService.getThemeById(id);
        validateReservationInTheme(theme);
        validateWaitingInTime(theme);
        themeRepository.deleteById(id);
    }

    private void validateReservationInTheme(Theme theme) {
        boolean isExistReservation = reservationQueryService.existsReservationInTheme(theme);
        if (isExistReservation) {
            throw new BadRequestException("이미 예약이 존재하는 테마입니다.");
        }
    }

    private void validateWaitingInTime(Theme theme) {
        boolean isExistWaiting = waitingQueryService.existsWaitingInTheme(theme);
        if (isExistWaiting) {
            throw new BadRequestException("이미 예약 대기가 존재하는 테마입니다.");
        }
    }
}
