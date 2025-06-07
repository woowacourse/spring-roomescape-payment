package roomescape.reservationtime.application;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.presentation.dto.request.ReservationTimeCreateWebRequest;
import roomescape.reservationtime.presentation.dto.response.AvailableReservationTimeWebResponse;
import roomescape.reservationtime.presentation.dto.response.ReservationTimeWebResponse;

@Service
@Transactional
@Slf4j
public class ReservationTimeApplicationService {

    private final ReservationTimeDataService reservationTimeDataService;

    public ReservationTimeApplicationService(final ReservationTimeDataService reservationTimeDataService) {
        this.reservationTimeDataService = reservationTimeDataService;
    }

    public ReservationTimeWebResponse create(final ReservationTimeCreateWebRequest request) {
        log.info("예약 시간 생성: startTime={}", request.startAt());

        ReservationTime newReservationTime = reservationTimeDataService.create(request.toReservationTime());

        log.info("예약 시간 생성 완료: id={}, startTime={}", newReservationTime.getId(), newReservationTime.getStartAt());

        return ReservationTimeWebResponse.from(newReservationTime);
    }

    public List<ReservationTimeWebResponse> findAll() {
        return reservationTimeDataService.findAll()
                .stream()
                .map(ReservationTimeWebResponse::from)
                .toList();
    }

    public List<AvailableReservationTimeWebResponse> findAvailable(
            final LocalDate date,
            final Long themeId
    ) {
        return reservationTimeDataService.findAvailable(date, themeId);
    }

    public void removeById(Long id) {
        log.info("예약 시간 삭제: id={}", id);

        reservationTimeDataService.delete(id);

        log.info("예약 시간 삭제 완료: id={}", id);
    }
}
