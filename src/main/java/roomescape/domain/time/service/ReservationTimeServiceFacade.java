package roomescape.domain.time.service;

import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.time.dto.ReservationTimeRequest;
import roomescape.domain.time.dto.ReservationTimeResponse;
import roomescape.domain.time.entity.ReservationTime;

@RequiredArgsConstructor
@Service
public class ReservationTimeServiceFacade {

    private final ReservationTimeService reservationTimeService;

    @Transactional
    public ReservationTimeResponse save(final ReservationTimeRequest request) {
        final LocalTime startAt = request.startAt();

        final ReservationTime savedReservationTime = reservationTimeService.save(startAt);

        return ReservationTimeResponse.from(savedReservationTime);
    }

    @Transactional
    public void deleteById(final Long id) {
        reservationTimeService.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeResponse> findAll() {
        final List<ReservationTime> reservationTimes = reservationTimeService.findAll();

        return reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }
}

