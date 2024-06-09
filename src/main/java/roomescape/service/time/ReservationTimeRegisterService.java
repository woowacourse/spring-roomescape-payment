package roomescape.service.time;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.reservationtime.ReservationTimeRequest;
import roomescape.dto.reservationtime.ReservationTimeResponse;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.time.module.ReservationTimeValidator;

@Service
@Transactional
public class ReservationTimeRegisterService {

    private final ReservationTimeValidator timeValidator;
    private final ReservationTimeRepository timeRepository;

    public ReservationTimeRegisterService(ReservationTimeValidator timeValidator,
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
