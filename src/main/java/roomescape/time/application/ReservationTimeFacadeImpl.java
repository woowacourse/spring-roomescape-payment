package roomescape.time.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.time.application.dto.CreateReservationTimeServiceRequest;
import roomescape.time.application.service.ReservationTimeCommandService;
import roomescape.time.application.service.ReservationTimeQueryService;
import roomescape.time.ui.dto.CreateReservationTimeWebRequest;
import roomescape.time.ui.dto.ReservationTimeResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationTimeFacadeImpl implements ReservationTimeFacade {

    private final ReservationTimeQueryService reservationTimeQueryService;
    private final ReservationTimeCommandService reservationTimeCommandService;

    @Override
    public List<ReservationTimeResponse> getAll() {
        return ReservationTimeResponse.from(
                reservationTimeQueryService.getAll());
    }

    @Override
    public ReservationTimeResponse create(final CreateReservationTimeWebRequest request) {
        log.info("[TIME] 예약 시간 생성 요청: {}", request);
        return ReservationTimeResponse.from(
                reservationTimeCommandService.create(
                        new CreateReservationTimeServiceRequest(
                                request.startAt())));
    }

    @Override
    public void delete(final Long id) {
        log.info("[TIME] 예약 시간 삭제 요청: id={}", id);
        reservationTimeCommandService.delete(id);
    }
}
