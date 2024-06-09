package roomescape.service.time;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.dto.reservationtime.ReservationTimeRequest;
import roomescape.dto.reservationtime.ReservationTimeResponse;
import roomescape.dto.reservationtime.TimeWithAvailableResponse;

@Service
public class ReservationTimeService {

    private final ReservationTimeRegisterService reservationTimeRegisterService;
    private final ReservationTimeSearchService reservationTimeSearchService;
    private final ReservationTimeDeleteService reservationTimeDeleteService;

    public ReservationTimeService(ReservationTimeRegisterService reservationTimeRegisterService,
                                  ReservationTimeSearchService reservationTimeSearchService,
                                  ReservationTimeDeleteService reservationTimeDeleteService
    ) {
        this.reservationTimeRegisterService = reservationTimeRegisterService;
        this.reservationTimeSearchService = reservationTimeSearchService;
        this.reservationTimeDeleteService = reservationTimeDeleteService;
    }

    public Long registerReservationTime(ReservationTimeRequest reservationTimeRequest) {
        return reservationTimeRegisterService.registerTime(reservationTimeRequest);
    }

    public ReservationTimeResponse findReservationTime(Long timeId) {
        return reservationTimeSearchService.findTime(timeId);
    }

    public List<ReservationTimeResponse> findAllReservationTimes() {
        return reservationTimeSearchService.findAllTimes();
    }

    public List<TimeWithAvailableResponse> getAvailableTimes(LocalDate date, Long themeId) {
        return reservationTimeSearchService.findAvailableTimes(date, themeId);
    }

    public void deleteReservationTime(Long timeId) {
        reservationTimeDeleteService.deleteTime(timeId);
    }
}
