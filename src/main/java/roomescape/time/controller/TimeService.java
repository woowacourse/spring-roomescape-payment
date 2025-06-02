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
import roomescape.time.service.TimeCommandService;
import roomescape.time.service.TimeQueryService;

@Service
public class TimeService {
    private final TimeCommandService timeCommandService;
    private final TimeQueryService timeQueryService;
    private final ThemeQueryService themeQueryService;

    public TimeService(
            final TimeCommandService timeCommandService,
            final TimeQueryService timeQueryService,
            final ThemeQueryService themeQueryService
    ) {
        this.timeCommandService = timeCommandService;
        this.timeQueryService = timeQueryService;
        this.themeQueryService = themeQueryService;
    }

    public ReservationTimeResponse createReservationTime(final CreateReservationTimeRequest request) {
        ReservationTime time = timeCommandService.createReservationTime(request.startAt());
        return ReservationTimeResponse.from(time);
    }

    public List<ReservationTimeResponse> findAllReservationTimes() {
        return ReservationTimeResponse.from(timeQueryService.findAll());
    }

    public void deleteReservationTimeById(final Long timeId) {
        timeCommandService.deleteTimeById(timeId);
    }

    public List<AvailableReservationTimeResponse> findAvailableReservationTimes(
            final AvailableReservationTimeRequest request
    ) {
        List<AvailableReservationTime> availableReservationTimes = timeQueryService.findAvailableReservationTimes(
                new ReservationDate(request.date()), themeQueryService.getById(request.themeId())
        );
        return AvailableReservationTimeResponse.from(availableReservationTimes);
    }
}
