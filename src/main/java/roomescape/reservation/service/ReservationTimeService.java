package roomescape.reservation.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.dto.request.ReservationTimeRequest;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.dto.response.ReservationTimesResponse;
import roomescape.system.exception.error.ErrorType;
import roomescape.system.exception.model.AssociatedDataExistsException;
import roomescape.system.exception.model.DataDuplicateException;
import roomescape.system.exception.model.NotFoundException;

@Service
@Transactional
public class ReservationTimeService {
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(
            final ReservationTimeRepository reservationTimeRepository,
            final ReservationRepository reservationRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationTime findTimeById(final Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorType.RESERVATION_TIME_NOT_FOUND,
                        String.format("예약 시간(ReservationTime) 정보가 존재하지 않습니다. [reservationTimeId: %d]", id)));
    }

    public ReservationTimesResponse findAllTimes() {
        List<ReservationTimeResponse> response = reservationTimeRepository.findAll()
                .stream()
                .map(ReservationTimeResponse::from)
                .toList();

        return new ReservationTimesResponse(response);
    }

    public ReservationTimeResponse addTime(final ReservationTimeRequest reservationTimeRequest) {
        validateTimeDuplication(reservationTimeRequest);
        ReservationTime reservationTime = reservationTimeRepository.save(reservationTimeRequest.toTime());

        return ReservationTimeResponse.from(reservationTime);
    }

    private void validateTimeDuplication(final ReservationTimeRequest reservationTimeRequest) {
        List<ReservationTime> duplicateReservationTimes = reservationTimeRepository.findByStartAt(
                reservationTimeRequest.startAt());

        if (!duplicateReservationTimes.isEmpty()) {
            throw new DataDuplicateException(ErrorType.TIME_DUPLICATED,
                    String.format("이미 존재하는 예약 시간(ReservationTime) 입니다. [startAt: %s]",
                            reservationTimeRequest.startAt()));
        }
    }

    public void removeTimeById(final Long id) {
        ReservationTime reservationTime = findTimeById(id);
        List<Reservation> usingTimeReservations = reservationRepository.findByReservationTime(reservationTime);

        if (!usingTimeReservations.isEmpty()) {
            throw new AssociatedDataExistsException(ErrorType.TIME_IS_USED_CONFLICT,
                    String.format("해당 예약 시간(ReservationTime) 에 예약이 존재하여 시간을 삭제할 수 없습니다. [timeId: %d]", id));
        }

        reservationTimeRepository.deleteById(id);
    }
}
