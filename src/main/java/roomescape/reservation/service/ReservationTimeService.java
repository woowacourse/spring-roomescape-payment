package roomescape.reservation.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.reservation.controller.dto.AvailableTimeResponse;
import roomescape.reservation.controller.dto.ReservationTimeResponse;
import roomescape.reservation.domain.AvailableTimes;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.service.dto.ReservationTimeCreate;

@Service
@Transactional(readOnly = true)
public class ReservationTimeService {

    private final ReservationRepository reservationRepository;

    private final ReservationTimeRepository reservationTimeRepository;

    public ReservationTimeService(ReservationRepository reservationRepository,
                                  ReservationTimeRepository reservationTimeRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
    }

    @Transactional
    public ReservationTimeResponse create(ReservationTimeCreate reservationTimeCreate) {
        LocalTime time = reservationTimeCreate.startAt();
        if (reservationTimeRepository.existsByStartAt(time)) {
            throw new BadRequestException(ErrorType.DUPLICATED_RESERVATION_TIME_ERROR);
        }

        ReservationTime reservationTime = new ReservationTime(time);
        return ReservationTimeResponse.from(reservationTimeRepository.save(reservationTime));
    }

    public List<ReservationTimeResponse> findAll() {
        return reservationTimeRepository.findAll()
                .stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(long timeId) {
        if (reservationRepository.existsByTimeId(timeId)) {
            throw new BadRequestException(ErrorType.RESERVATION_NOT_DELETED);
        }
        reservationTimeRepository.deleteById(timeId);
    }

    public List<AvailableTimeResponse> findAvailableTimes(LocalDate date, long themeId) {
        List<ReservationTime> times = reservationTimeRepository.findAll();
        Set<ReservationTime> reservedTimes = reservationTimeRepository.findReservedTime(date, themeId);

        return AvailableTimes.of(times, reservedTimes).getAvailableTimes()
                .stream()
                .map(AvailableTimeResponse::from)
                .toList();
    }
}
