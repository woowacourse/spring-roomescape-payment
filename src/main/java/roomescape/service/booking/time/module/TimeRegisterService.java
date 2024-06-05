package roomescape.service.booking.time.module;

import java.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.reservationtime.ReservationTimeRequest;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationTimeRepository;

@Service
@Transactional
public class TimeRegisterService {

    private final ReservationTimeRepository reservationTimeRepository;

    public TimeRegisterService(ReservationTimeRepository reservationTimeRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
    }

    public Long registerTime(ReservationTimeRequest reservationTimeRequest) {
        validateTimeDuplicate(reservationTimeRequest.startAt());
        ReservationTime reservationTime = reservationTimeRequest.toEntity();
        return reservationTimeRepository.save(reservationTime).getId();
    }

    private void validateTimeDuplicate(LocalTime time) {
        if (reservationTimeRepository.existsByStartAt(time)) {
            throw new RoomEscapeException(
                    ErrorCode.TIME_NOT_REGISTER_BY_DUPLICATE,
                    "등록 시간 = " + time
            );
        }
    }
}
