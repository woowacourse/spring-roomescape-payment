package roomescape.time.controller;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.schedule.domain.ReservationDate;
import roomescape.theme.service.ThemeQueryService;
import roomescape.time.controller.dto.AvailableReservationTimeRequest;
import roomescape.time.controller.dto.AvailableReservationTimeResponse;
import roomescape.time.controller.dto.CreateReservationTimeRequest;
import roomescape.time.controller.dto.ReservationTimeResponse;
import roomescape.time.domain.AvailableReservationTime;
import roomescape.time.domain.ReservationTime;
import roomescape.time.service.ReservationTimeCommandService;
import roomescape.time.service.ReservationTimeQueryService;

@Service
public class ReservationTimeService {
    private final ReservationTimeCommandService reservationTimeCommandService;
    private final ReservationTimeQueryService reservationTimeQueryService;
    private final ThemeQueryService themeQueryService;

    public ReservationTimeService(
            final ReservationTimeCommandService reservationTimeCommandService,
            final ReservationTimeQueryService reservationTimeQueryService,
            final ThemeQueryService themeQueryService
    ) {
        this.reservationTimeCommandService = reservationTimeCommandService;
        this.reservationTimeQueryService = reservationTimeQueryService;
        this.themeQueryService = themeQueryService;
    }

    public ReservationTimeResponse createReservationTime(final CreateReservationTimeRequest request) {
        ReservationTime time = reservationTimeCommandService.createReservationTime(request.startAt());
        return ReservationTimeResponse.from(time);
    }

    public List<ReservationTimeResponse> findAllReservationTimes() {
        return ReservationTimeResponse.from(reservationTimeQueryService.findAll());
    }

    public void deleteReservationTimeById(final Long timeId) {
        reservationTimeCommandService.deleteTimeById(timeId);
    }

    public List<AvailableReservationTimeResponse> findAvailableReservationTimes(
            final AvailableReservationTimeRequest request
    ) {
        List<AvailableReservationTime> availableReservationTimes = reservationTimeQueryService.findAvailableReservationTimes(
                new ReservationDate(request.date()), themeQueryService.getById(request.themeId())
        );
        return AvailableReservationTimeResponse.from(availableReservationTimes);
    }
}
