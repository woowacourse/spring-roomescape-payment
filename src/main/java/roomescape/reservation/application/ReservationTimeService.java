package roomescape.reservation.application;

import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.resource.AlreadyExistException;
import roomescape.exception.resource.ResourceInUseException;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.ui.dto.request.CreateReservationTimeRequest;
import roomescape.reservation.ui.dto.response.ReservationTimeResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;

    @Transactional
    public ReservationTimeResponse create(final CreateReservationTimeRequest request) {
        final LocalTime startAt = request.startAt();

        log.info("예약 시간 생성 요청 - startAt: {}", startAt);

        final List<ReservationTime> founds = reservationTimeRepository.findAllByStartAt(startAt);
        if (!founds.isEmpty()) {
            log.warn("중복 예약 시간 존재 - startAt: {}", startAt);
            throw new AlreadyExistException("해당 예약 시간이 이미 존재합니다. startAt = " + startAt);
        }

        final ReservationTime saved = reservationTimeRepository.save(ReservationTime.from(startAt));

        log.info("예약 시간 생성 완료 - id: {}, startAt: {}", saved.getId(), saved.getStartAt());

        return ReservationTimeResponse.from(saved);
    }

    @Transactional
    public void deleteById(final Long timeId) {
        log.info("예약 시간 삭제 요청 - id: {}", timeId);

        reservationTimeRepository.getById(timeId);  // 존재 확인용

        try {
            reservationTimeRepository.deleteById(timeId);
            log.info("예약 시간 삭제 성공 - id: {}", timeId);
        } catch (final DataIntegrityViolationException e) {
            log.warn("예약 시간 삭제 실패 - 예약 참조 중인 시간, id: {}", timeId);
            throw new ResourceInUseException("해당 예약 시간을 사용하고 있는 예약이 존재합니다. id = " + timeId);
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeResponse> findAll() {
        log.info("예약 시간 전체 조회 요청");

        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();

        log.info("예약 시간 전체 조회 완료 - 개수: {}", reservationTimes.size());

        return reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }
}
