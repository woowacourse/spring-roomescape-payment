package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.dto.request.ReservationTimeRequest;
import roomescape.reservation.dto.response.ReservationTimeInfoResponse;
import roomescape.reservation.dto.response.ReservationTimeInfosResponse;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.dto.response.ReservationTimesResponse;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Service
@Transactional
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(
            ReservationTimeRepository reservationTimeRepository,
            ReservationRepository reservationRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public ReservationTime findTimeById(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeException(ErrorType.RESERVATION_TIME_NOT_FOUND,
                        String.format("[reservationTimeId: %d]", id), HttpStatus.BAD_REQUEST));
    }

    @Transactional(readOnly = true)
    public ReservationTimesResponse findAllTimes() {
        List<ReservationTimeResponse> response = reservationTimeRepository.findAll()
                .stream()
                .map(ReservationTimeResponse::from)
                .toList();

        return new ReservationTimesResponse(response);
    }

    public ReservationTimeResponse addTime(ReservationTimeRequest reservationTimeRequest) {
        validateTimeDuplication(reservationTimeRequest);
        ReservationTime reservationTime = reservationTimeRepository.save(reservationTimeRequest.toTime());

        return ReservationTimeResponse.from(reservationTime);
    }

    private void validateTimeDuplication(ReservationTimeRequest reservationTimeRequest) {
        List<ReservationTime> duplicateReservationTimes = reservationTimeRepository.findByStartAt(
                reservationTimeRequest.startAt());

        if (!duplicateReservationTimes.isEmpty()) {
            throw new RoomEscapeException(ErrorType.TIME_DUPLICATED,
                    String.format("[startAt: %s]", reservationTimeRequest.startAt()), HttpStatus.CONFLICT);
        }
    }

    public void removeTimeById(Long id) {
        ReservationTime reservationTime = findTimeById(id);
        List<Reservation> usingTimeReservations = reservationRepository.findByReservationTime(reservationTime);

        if (!usingTimeReservations.isEmpty()) {
            throw new RoomEscapeException(ErrorType.TIME_IS_USED_CONFLICT, String.format("[timeId: %d]", id),
                    HttpStatus.CONFLICT);
        }

        reservationTimeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ReservationTimeInfosResponse findAllAvailableTimesByDateAndTheme(LocalDate date, Long themeId) {
        List<ReservationTime> allTimes = reservationTimeRepository.findAll();
        List<Reservation> reservations = reservationRepository.findByThemeId(themeId);

        List<ReservationTimeInfoResponse> response = allTimes.stream()
                .map(time -> new ReservationTimeInfoResponse(time.getId(), time.getStartAt(),
                        isReservationBooked(reservations, date, time)))
                .toList();

        return new ReservationTimeInfosResponse(response);
    }

    private boolean isReservationBooked(List<Reservation> reservations, LocalDate date, ReservationTime time) {
        return reservations.stream()
                .anyMatch(reservation -> reservation.isSameDateAndTime(date, time));
    }
}
