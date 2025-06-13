package roomescape.time.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.custom.AlreadyInUseException;
import roomescape.common.exception.custom.EntityNotFoundException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeId;
import roomescape.time.dto.request.ReservationTimeRequest;
import roomescape.time.dto.response.ReservationTimeResponse;
import roomescape.time.repository.ReservationTimeRepository;

@Slf4j
@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(
            final ReservationTimeRepository reservationTimeRepository,
            final ReservationRepository reservationRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReservationTimeResponse create(final ReservationTimeRequest request) {
        if (reservationTimeRepository.existsByStartAt(request.startAt())) {
            throw new AlreadyInUseException("이미 존재하는 시간입니다.");
        }
        ReservationTime reservationTime = request.toEntity();
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        log.info("예약 시간 저장 완료: timeId={}", savedReservationTime.getId());
        return ReservationTimeResponse.from(savedReservationTime);
    }

    public List<ReservationTimeResponse> getAll() {
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(final Long id) {
        ReservationTimeId timeId = new ReservationTimeId(id);
        if (reservationRepository.existsByTimeId(timeId)) {
            throw new AlreadyInUseException("예약 데이터가 존재하는 시간입니다.");
        }
        if (!reservationTimeRepository.existsById(timeId)) {
            throw new EntityNotFoundException("존재하지 않는 예약 시간입니다.");
        }
        reservationTimeRepository.deleteById(timeId);
        log.info("예약 시간 삭제 완료: timeId={}", id);
    }
}