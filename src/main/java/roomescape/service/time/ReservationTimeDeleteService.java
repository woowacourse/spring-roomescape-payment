package roomescape.service.time;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.time.ReservationTime;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.time.module.ReservationTimeValidator;

@Service
@Transactional
public class ReservationTimeDeleteService {

    private final ReservationTimeValidator timeValidator;
    private final ReservationTimeRepository timeRepository;

    public ReservationTimeDeleteService(ReservationTimeValidator timeValidator,
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
