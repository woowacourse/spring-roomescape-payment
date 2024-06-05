package roomescape.reservation.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepository;

@Service
public class ReservationFindMineService {
    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public ReservationFindMineService(ReservationRepository reservationRepository,
                                      WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    public List<MyReservationResponse> findReservations(Long memberId) {
        return reservationRepository.findByMemberId(memberId)
                .stream()
                .map(MyReservationResponse::from)
                .toList();
    }

    public List<MyReservationResponse> findWaitings(Long memberId) {
        return waitingRepository.findByMemberId(memberId)
                .stream()
                .map(waiting -> MyReservationResponse.from(waiting, countOrderOfWaiting(waiting)))
                .toList();
    }

    private Long countOrderOfWaiting(Waiting waiting) {
        return waitingRepository.countByReservationAndCreatedAtLessThanEqual(
                waiting.getReservation(), waiting.getCreatedAt());
    }
}
