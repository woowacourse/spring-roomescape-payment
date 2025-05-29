package roomescape.application.reservation.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.reservation.query.dto.AvailableReservationTimeResult;
import roomescape.application.reservation.query.dto.ReservationTimeResult;
import roomescape.domain.reservation.DailyThemeReservations;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ReservationTimeQueryService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeQueryService(final ReservationTimeRepository reservationTimeRepository,
                                       final ReservationRepository reservationRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationTimeResult> findAll() {
        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return reservationTimes.stream()
                .map(ReservationTimeResult::from)
                .toList();
    }

    public List<AvailableReservationTimeResult> findAvailableTimesByThemeIdAndDate(final Long themeId, final LocalDate date) {
        final List<ReservationTime> timeSlots = reservationTimeRepository.findAll();
        final Set<ReservationTime> bookedTimes = getBookedTimes(themeId, date);
        return timeSlots.stream()
                .map(reservationTime -> toAvailableTimeResult(reservationTime, bookedTimes))
                .toList();
    }

    private Set<ReservationTime> getBookedTimes(final Long themeId, final LocalDate date) {
        final List<Reservation> reservations = reservationRepository.findByThemeIdAndDate(themeId, date);
        return new DailyThemeReservations(reservations, themeId, date)
                .calculateBookedTimes();
    }

    private AvailableReservationTimeResult toAvailableTimeResult(final ReservationTime time,
                                                                 final Set<ReservationTime> bookedTimes) {
        return new AvailableReservationTimeResult(
                time.getId(),
                time.getStartAt(),
                bookedTimes.contains(time)
        );
    }
}
