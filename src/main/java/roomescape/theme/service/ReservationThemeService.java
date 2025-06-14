package roomescape.theme.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorCode;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.ReservationTheme;
import roomescape.theme.dto.ReservationThemeRequest;
import roomescape.theme.dto.ReservationThemeResponse;
import roomescape.theme.repository.ReservationThemeRepository;

@Service
public class ReservationThemeService {

    private final ReservationRepository reservationRepository;
    private final ReservationThemeRepository reservationThemeRepository;

    public ReservationThemeService(
            final ReservationRepository reservationRepository,
            final ReservationThemeRepository reservationThemeRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationThemeRepository = reservationThemeRepository;
    }

    public List<ReservationThemeResponse> findReservationThemes() {
        List<ReservationTheme> reservationThemes = reservationThemeRepository.findAll();
        return reservationThemes.stream().map(ReservationThemeResponse::from).toList();
    }

    public List<ReservationThemeResponse> findPopularThemes() {
        List<ReservationTheme> popularReservationThemes = reservationThemeRepository.findWeeklyThemeOrderByCountDesc();
        return popularReservationThemes.stream().map(ReservationThemeResponse::from).toList();
    }

    public ReservationThemeResponse addReservationTheme(final ReservationThemeRequest request) {
        final ReservationTheme reservationTheme = ReservationTheme.builder()
                .name(request.name())
                .description(request.description())
                .thumbnail(request.thumbnail())
                .build();
        validateUniqueThemes(reservationTheme);
        ReservationTheme saved = reservationThemeRepository.save(reservationTheme);
        return ReservationThemeResponse.from(saved);
    }

    public void removeReservationTheme(final long id) {
        validateExistTheme(id);
        validateExistReservation(id);
        reservationThemeRepository.deleteById(id);
    }

    public ReservationTheme getById(final long id) {
        return reservationThemeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.THEME_NOT_FOUND));
    }

    private void validateExistTheme(final long id) {
        if (!reservationThemeRepository.existsById(id)) {
            throw new BadRequestException(ErrorCode.THEME_NOT_FOUND);
        }
    }

    private void validateExistReservation(final long id) {
        if (reservationRepository.existByThemeId(id)) {
            throw new BadRequestException(ErrorCode.THEME_HAS_RESERVATION);
        }
    }

    private void validateUniqueThemes(final ReservationTheme reservationTheme) {
        if (reservationThemeRepository.existsByName(reservationTheme.getName())) {
            throw new BadRequestException(ErrorCode.THEME_ALREADY_EXISTS);
        }
    }
}
