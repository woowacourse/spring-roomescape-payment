package roomescape.reservation.service.module;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Service;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingRepository;

@Service
public class WaitingPlanService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public WaitingPlanService(ReservationRepository reservationRepository, WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    public void validateSaveWaiting(Waiting waiting) {
        validateMemberWaitingUnique(waiting);
        validateWaitingAvailable(waiting);
    }

    public void validateApproveReservation(Waiting waiting) {
        Optional<Reservation> reservation = findReservation(waiting.getDate(), waiting.getTime(), waiting.getTheme());
        if (reservation.isPresent()) {
            throw new IllegalArgumentException("이미 확정된 예약이 있습니다.");
        }
        if (findWaitingByStatus(waiting, Status.PAYMENT_PENDING).isPresent()) {
            throw new IllegalArgumentException("이미 결제 대기 중인 예약이 있습니다.");
        }
    }

    private void validateMemberWaitingUnique(Waiting waiting) {
        if (findMemberWaiting(waiting).isPresent()) {
            throw new IllegalArgumentException("이미 회원이 예약 대기한 내역이 있습니다.");
        }
    }

    private void validateWaitingAvailable(Waiting waiting) {
        Optional<Reservation> reservation = findReservation(waiting.getDate(), waiting.getTime(), waiting.getTheme());
        if (reservation.isEmpty()) {
            throw new IllegalArgumentException("추가된 예약이 없어 대기 등록을 할 수 없습니다.");
        }
    }

    private Optional<Waiting> findWaitingByStatus(Waiting waiting, Status status) {
        return waitingRepository.findFirstByDateAndReservationTimeAndThemeAndStatus(
                waiting.getDate(),
                waiting.getTime(),
                waiting.getTheme(),
                status
        );
    }

    private Optional<Waiting> findMemberWaiting(Waiting waiting) {
        return waitingRepository.findFirstByDateAndReservationTimeAndThemeAndMember(
                waiting.getDate(),
                waiting.getTime(),
                waiting.getTheme(),
                waiting.getMember()
        );
    }

    private Optional<Reservation> findReservation(
            LocalDate date,
            ReservationTime time,
            Theme theme
    ) {
        return reservationRepository.findFirstByDateAndReservationTimeAndTheme(date, time, theme);
    }
}
