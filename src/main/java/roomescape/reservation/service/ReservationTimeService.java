package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.AlreadyInUseException;
import roomescape.common.exception.EntityNotFoundException;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.service.dto.request.ReservationTimeRequest;
import roomescape.reservation.service.dto.response.ReservationTimeResponse;

import java.util.List;

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

    public List<ReservationTimeResponse> getAll() {
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();

        return reservationTimes.stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    @Transactional
    public ReservationTimeResponse create(final ReservationTimeRequest request) {
        ReservationTime reservationTime = request.toEntity();
        if (isAlreadyExist(reservationTime)) {
            throw new AlreadyInUseException("이미 존재하는 시간입니다.");
        }

        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        return ReservationTimeResponse.from(savedReservationTime);
    }

    private boolean isAlreadyExist(ReservationTime time) {
        return reservationTimeRepository.existsByStartAt(time.getStartAt());
    }

    @Transactional
    public void delete(final Long id) {
        if (!reservationTimeRepository.existsById(id)) {
            throw new EntityNotFoundException("존재하지 않는 예약 시간입니다.");
        }
        if (reservationRepository.existsByTimeId(id)) {
            throw new AlreadyInUseException("사용 중인 예약 시간은 삭제할 수 없습니다.");
        }
        reservationTimeRepository.deleteById(id);
    }
}
