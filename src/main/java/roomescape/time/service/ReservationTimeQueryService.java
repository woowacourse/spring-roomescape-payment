package roomescape.time.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.schedule.domain.ReservationDate;
import roomescape.schedule.repository.ReservationScheduleRepository;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.AvailableReservationTime;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

@Service
@Transactional(readOnly = true)
public class ReservationTimeQueryService {
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationScheduleRepository reservationScheduleRepository;

    public ReservationTimeQueryService(
            final ReservationTimeRepository reservationTimeRepository,
            final ReservationScheduleRepository reservationScheduleRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationScheduleRepository = reservationScheduleRepository;
    }

    public List<AvailableReservationTime> findAvailableReservationTimes(
            ReservationDate date,
            Theme theme
    ) {
        return reservationScheduleRepository.findAllAvailableReservationSchedules(date.date(), theme.getId());
    }

    public List<ReservationTime> findAll() {
        return reservationTimeRepository.findAll();
    }
}
