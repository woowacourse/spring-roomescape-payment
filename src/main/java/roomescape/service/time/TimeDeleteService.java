package roomescape.service.time;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.time.ReservationTime;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.time.module.TimeValidator;

@Service
@Transactional
public class TimeDeleteService {

    private final TimeValidator timeValidator;
    private final ReservationTimeRepository timeRepository;

    public TimeDeleteService(TimeValidator timeValidator,
                             ReservationTimeRepository timeRepository
    ) {
        this.timeValidator = timeValidator;
        this.timeRepository = timeRepository;
    }

    public void deleteTime(Long timeId) {
        ReservationTime reservationTime = timeRepository.findByIdOrThrow(timeId);
        timeValidator.validateDeletable(reservationTime);
        timeRepository.delete(reservationTime);
    }
}
