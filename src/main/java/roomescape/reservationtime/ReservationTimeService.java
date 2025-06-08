package roomescape.reservationtime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.booking.reservation.ReservationService;
import roomescape.exception.custom.reason.reservationtime.ReservationTimeConflictException;
import roomescape.exception.custom.reason.reservationtime.ReservationTimeNotFoundException;
import roomescape.exception.custom.reason.reservationtime.ReservationTimeUsedException;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.Theme;
import roomescape.theme.ThemeService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationService reservationService;
    private final ThemeService themeService;

    @Transactional
    public ReservationTimeResponse create(final ReservationTimeRequest request) {
        validateDuplication(request);

        final ReservationTime reservationTime = new ReservationTime(request.startAt());
        final ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        log.info("[{}] EVENT: RESERVATION_TIME_CREATED, id={}, startAt={}",
                MDC.get("requestId"),
                savedReservationTime.getId(),
                savedReservationTime.getStartAt());
        return ReservationTimeResponse.from(savedReservationTime);
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeResponse> getAll() {
        return reservationTimeRepository.findAll().stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationTime getById(final Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(ReservationTimeNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<AvailableReservationTimeResponse> getAllAvailableTimes(final Long themeId, final LocalDate date) {
        final List<ReservationTime> times = reservationTimeRepository.findAll();
        final Theme theme = themeService.getById(themeId);

        final Set<ReservationTime> reservationTimesByThemeAndDate = reservationService.getAllByThemeAndDate(theme, date).stream()
                .map(reservation -> reservation.getSchedule().getReservationTime())
                .collect(Collectors.toSet());

        return times.stream()
                .map(reservationTime ->
                        AvailableReservationTimeResponse.from(
                                reservationTime,
                                reservationTimesByThemeAndDate.contains(reservationTime)
                        )
                )
                .toList();
    }

    @Transactional
    public void deleteById(final Long id) {
        final ReservationTime reservationTime = reservationTimeRepository.findById(id)
                .orElseThrow(ReservationTimeNotFoundException::new);

        if (reservationService.existsByReservationTime(reservationTime)) {
            throw new ReservationTimeUsedException();
        }

        reservationTimeRepository.delete(reservationTime);
        log.info("[{}] EVENT: RESERVATION_TIME_DELETED, id={}, startAt={}",
                MDC.get("requestId"),
                reservationTime.getId(),
                reservationTime.getStartAt());
    }

    private void validateDuplication(final ReservationTimeRequest request) {
        if (reservationTimeRepository.existsByStartAt(request.startAt())) {
            log.warn("[{}] EVENT: RESERVATION_TIME_CREATE_FAILED - DUPLICATED, time={}",
                    MDC.get("requestId"),
                    request.startAt());
            throw new ReservationTimeConflictException();
        }
    }
}
