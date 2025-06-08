package roomescape.reservationtime.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.global.config.Performance;
import roomescape.global.exception.ReservationException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.repository.ReservationTimeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeResponse saveTime(final ReservationTimeRequest request) {
        ReservationTime reservationTime = reservationTimeRepository.save(ReservationTime.from(request.startAt()));
        log.info("[예약 시간 추가 성공] timeId: {}", reservationTime.getId());
        return new ReservationTimeResponse(reservationTime);
    }

    @Performance
    public List<ReservationTimeResponse> findAll() {
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return reservationTimes.stream()
                .map(ReservationTimeResponse::new)
                .toList();
    }

    public void delete(final Long timeId) {
        if (reservationRepository.existsByTimeId(timeId)) {
            log.warn("[예약 시간 삭제 실패] 예약 시간 사용 중 - timeId: {}", timeId);
            throw new ReservationException("해당 시간으로 예약된 건이 존재합니다.");
        }
        reservationTimeRepository.deleteById(timeId);
        log.info("[예약 시간 삭제 성공] timeId: {}", timeId);
    }
}
