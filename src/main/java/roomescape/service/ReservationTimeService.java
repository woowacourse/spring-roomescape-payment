package roomescape.service;

import static roomescape.exception.RoomescapeExceptionCode.RESERVATION_ALREADY_EXISTS;
import static roomescape.exception.RoomescapeExceptionCode.RESERVATION_TIME_NOT_FOUND;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationTime;
import roomescape.dto.reservation.AvailableReservationTimeResponse;
import roomescape.dto.reservation.AvailableReservationTimeSearch;
import roomescape.dto.reservation.ReservationTimeResponse;
import roomescape.exception.RoomescapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;

@Service
@Transactional
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(final ReservationTimeRepository reservationTimeRepository,
                                  final ReservationRepository reservationRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationTimeResponse create(final ReservationTime reservationTime) {
        final ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        return ReservationTimeResponse.from(savedReservationTime);
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeResponse> findAll() {
        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationTimeResponse findById(final Long id) {
        final ReservationTime reservationTime = reservationTimeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(RESERVATION_TIME_NOT_FOUND));
        return ReservationTimeResponse.from(reservationTime);
    }

    public void delete(final Long id) {
        final ReservationTime reservationTime = reservationTimeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(RESERVATION_TIME_NOT_FOUND));
        validateHasReservation(reservationTime);
        reservationTimeRepository.deleteById(reservationTime.getId());
    }

    private void validateHasReservation(final ReservationTime reservationTime) {
        final int reservationCount = reservationRepository.countByTimeId(reservationTime.getId());
        if (reservationCount > 0) {
            throw new RoomescapeException(RESERVATION_ALREADY_EXISTS);
        }
    }

    public List<AvailableReservationTimeResponse> findAvailableReservationTimes(
            final AvailableReservationTimeSearch condition
    ) {
        final Set<Long> reservedTimeIds = reservationRepository.findByDateAndThemeId(condition.date(), condition.themeId())
                .stream()
                .map(Reservation::getTime)
                .map(ReservationTime::getId)
                .collect(Collectors.toUnmodifiableSet());
        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return reservationTimes.stream()
                .map(reservationTime -> {
                    boolean isReserved = reservedTimeIds.contains(reservationTime.getId());
                    return AvailableReservationTimeResponse.of(reservationTime, isReserved);
                })
                .collect(Collectors.toList());
    }
}
