package roomescape.time.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorCode;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.dto.AvailableReservationTimeResponse;
import roomescape.time.dto.ReservationTimeRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.repository.ReservationTimeRepository;

@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(
            final ReservationTimeRepository reservationTimeRepository,
            final ReservationRepository reservationRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationTimeResponse addReservationTime(final ReservationTimeRequest request) {
        ReservationTime reservationTime = new ReservationTime(request.startAt());
        validateUniqueReservationTime(reservationTime);
        ReservationTime saved = reservationTimeRepository.save(reservationTime);
        return ReservationTimeResponse.from(saved);
    }

    public void removeReservationTime(final long id) {
        validateExistTime(id);
        validateExistReservation(id);
        reservationTimeRepository.deleteById(id);
    }

    public List<ReservationTimeResponse> findReservationTimes() {
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return reservationTimes.stream().map(ReservationTimeResponse::from).toList();
    }

    public ReservationTime getById(final long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.TIME_NOT_FOUND));
    }

    private void validateUniqueReservationTime(final ReservationTime reservationTime) {
        final LocalTime startAt = reservationTime.getStartAt();
        if (reservationTimeRepository.existsByStartAt(startAt)) {
            throw new BadRequestException(ErrorCode.TIME_ALREADY_EXISTS);
        }
    }

    private void validateExistReservation(final long id) {
        if (reservationRepository.existByTimeId(id)) {
            throw new BadRequestException(ErrorCode.TIME_HAS_RESERVATION);
        }
    }

    private void validateExistTime(final long id) {
        if (!reservationTimeRepository.existsById(id)) {
            throw new BadRequestException(ErrorCode.TIME_NOT_FOUND);
        }
    }

    public List<AvailableReservationTimeResponse> getAvailableTimes(final LocalDate date, final long themeId) {
        final List<ReservationTime> bookedReservationTimes = reservationTimeRepository.findAvailableTimesByDateAndThemeId(
                date, themeId);
        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return reservationTimes.stream()
                .map(reservationTime -> AvailableReservationTimeResponse.of(
                        reservationTime,
                        !bookedReservationTimes.contains(reservationTime)
                ))
                .toList();
    }
}
