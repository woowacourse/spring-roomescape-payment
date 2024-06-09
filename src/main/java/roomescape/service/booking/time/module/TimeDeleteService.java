package roomescape.service.booking.time.module;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.time.ReservationTime;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;

@Service
@Transactional
public class TimeDeleteService {

    private final ReservationTimeRepository timeRepository;
    private final ReservationRepository reservationRepository;

    public TimeDeleteService(ReservationTimeRepository reservationTimeRepository,
                             ReservationRepository reservationRepository
    ) {
        this.timeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public void deleteTime(Long timeId) {
        ReservationTime reservationTime = timeRepository.findByIdOrThrow(timeId);
        validateDeletable(reservationTime);
        timeRepository.delete(reservationTime);
    }

    private void validateDeletable(ReservationTime reservationTime) {
        if (reservationRepository.existsByTimeId(reservationTime.getId())) {
            throw new RoomEscapeException(
                    ErrorCode.TIME_NOT_DELETE_BY_EXIST_TIME,
                    "예약 시간 = " + reservationTime.getStartAt()
            );
        }
    }
}
