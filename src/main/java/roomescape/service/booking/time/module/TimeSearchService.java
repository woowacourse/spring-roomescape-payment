package roomescape.service.booking.time.module;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.reservationtime.ReservationTimeResponse;
import roomescape.dto.reservationtime.TimeWithAvailableResponse;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;

@Service
@Transactional(readOnly = true)
public class TimeSearchService {

    private final ReservationTimeRepository timeRepository;
    private final ReservationRepository reservationRepository;

    public TimeSearchService(ReservationTimeRepository timeRepository,
                             ReservationRepository reservationRepository) {
        this.timeRepository = timeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationTimeResponse findTime(Long timeId) {
        ReservationTime reservationTime = timeRepository.findByIdOrThrow(timeId);
        return ReservationTimeResponse.from(reservationTime);
    }

    public List<ReservationTimeResponse> findAllTimes() {
        return timeRepository.findAll()
                .stream()
                .map(ReservationTimeResponse::from).toList();
    }

    public List<TimeWithAvailableResponse> findAvailableTimes(LocalDate date, Long themeId) {
        return timeRepository.findAll()
                .stream()
                .map(reservationTime -> createTimeWithAvailableResponses(date, themeId, reservationTime))
                .toList();
    }

    private TimeWithAvailableResponse createTimeWithAvailableResponses(LocalDate date,
                                                                       Long themeId,
                                                                       ReservationTime reservationTime
    ) {
        boolean isBooked = reservationRepository.existsByDateAndTimeIdAndThemeId(
                date, reservationTime.getId(), themeId);

        return TimeWithAvailableResponse.from(reservationTime, isBooked);
    }
}
