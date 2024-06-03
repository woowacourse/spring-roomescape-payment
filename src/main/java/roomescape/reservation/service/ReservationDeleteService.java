package roomescape.reservation.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.BadArgumentRequestException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepository;

@Service
public class ReservationDeleteService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public ReservationDeleteService(ReservationRepository reservationRepository, WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    @Transactional
    public void deleteReservation(Long reservationId) {
        Reservation reservation = findReservation(reservationId);
        validateIsAfterFromNow(reservation);

        findHighPriorityWaiting(reservationId).ifPresentOrElse(
                Waiting::confirmReservation,
                () -> reservationRepository.deleteById(reservationId));
    }

    private Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new BadArgumentRequestException("해당 예약을 찾을 수 없습니다."));
    }

    private Optional<Waiting> findHighPriorityWaiting(Long reservationId) {
        return waitingRepository.findTopByReservationIdOrderByCreatedAtAsc(reservationId);
    }

    private void validateIsAfterFromNow(Reservation reservation) {
        if (reservation.isBefore(LocalDateTime.now())) {
            throw new BadArgumentRequestException("예약은 현재 날짜 이후여야 합니다.");
        }
    }
}
