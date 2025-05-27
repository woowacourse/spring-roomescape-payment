package roomescape.wait.service;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.wait.domain.ReservationWait;
import roomescape.wait.repository.ReservationWaitRepository;


@Service
@Transactional(readOnly = true)
public class ReservationWaitQueryService {

    private final ReservationWaitRepository reservationWaitRepository;

    public ReservationWaitQueryService(final ReservationWaitRepository reservationWaitRepository) {
        this.reservationWaitRepository = reservationWaitRepository;
    }

    public List<ReservationWait> findAllMyWaitReservation(final Long memberId) {
        return reservationWaitRepository.findAllByMember_id(memberId);
    }

    public List<ReservationWait> findAll() {
        return reservationWaitRepository.findAll();
    }

    public ReservationWait getById(final Long waitId) {
        return reservationWaitRepository.findById(waitId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 대기입니다."));
    }
}
