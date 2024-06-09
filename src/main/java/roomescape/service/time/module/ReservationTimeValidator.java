package roomescape.service.time.module;

import java.time.LocalTime;
import org.springframework.stereotype.Component;
import roomescape.domain.time.ReservationTime;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;

@Component
public class ReservationTimeValidator {

    private final ReservationTimeRepository timeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeValidator(ReservationTimeRepository timeRepository, ReservationRepository reservationRepository) {
        this.timeRepository = timeRepository;
        this.reservationRepository = reservationRepository;
    }

    public void validateTimeDuplicate(LocalTime time) {
        if (timeRepository.existsByStartAt(time)) {
            throw new RoomEscapeException(
                    ErrorCode.TIME_NOT_REGISTER_BY_DUPLICATE,
                    "등록 시간 = " + time
            );
        }
    }

    public void validateDeletable(ReservationTime reservationTime) {
        if (reservationRepository.existsByTimeId(reservationTime.getId())) {
            throw new RoomEscapeException(
                    ErrorCode.TIME_NOT_DELETE_BY_EXIST_TIME,
                    "예약 시간 = " + reservationTime.getStartAt()
            );
        }
    }
}
