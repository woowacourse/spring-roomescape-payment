package roomescape.service.time;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.time.ReservationTimeRequest;
import roomescape.dto.time.ReservationTimeResponse;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.time.module.TimeValidator;

@Service
@Transactional
public class TimeRegisterService {

    private final TimeValidator timeValidator;
    private final ReservationTimeRepository timeRepository;

    public TimeRegisterService(TimeValidator timeValidator,
                               ReservationTimeRepository timeRepository
    ) {
        this.timeValidator = timeValidator;
        this.timeRepository = timeRepository;
    }

    public ReservationTimeResponse registerTime(ReservationTimeRequest reservationTimeRequest) {
        timeValidator.validateTimeDuplicate(reservationTimeRequest.startAt());
        ReservationTime reservationTime = reservationTimeRequest.toEntity();
        return ReservationTimeResponse.from(timeRepository.save(reservationTime));
    }
}
