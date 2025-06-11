package roomescape.application.reservation.command;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.WaitingRepository;
import roomescape.infrastructure.error.exception.WaitingException;

@Service
@Transactional
public class AutoWaitingPromotionService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    public AutoWaitingPromotionService(WaitingRepository waitingRepository,
                                       ReservationRepository reservationRepository,
                                       Clock clock) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
    }

    /**
     * 예약 삭제 이벤트 발생 시 자동 승격을 수행하는 로직입니다.
     * 운영 환경에서는 @Async + @TransactionalEventListener(AFTER_COMMIT)로 별도 트랜잭션에서 실행됩니다.
     * 하지만 테스트 환경에서는 SyncTaskExecutor 인해 동기 실행되므로,
     * AFTER_COMMIT 후 트랜잭션 정리 이전의 '중간 상태'에서 실행되며 트랜잭션 문제가 발생할 수 있습니다.
     * 이를 방지하기 위해 Propagation.REQUIRES_NEW 사용하여 항상 새로운 트랜잭션에서 동작하도록 합니다.
     * 자세한 테스트 코드는 test 패키지 참고:
     * (test/java/roomescape/application/reservation/event/DeleteReservationEventListenerTest.java)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void promote(LocalDate reservationDate, Long reservationTimeId, Long themeId) {
        validateExistsReservation(reservationDate, reservationTimeId, themeId);
        List<Waiting> waitings = getWaitings(reservationDate, reservationTimeId, themeId);
        if (waitings.isEmpty()) {
            return;
        }
        waitings.stream()
                .findFirst()
                .ifPresent(this::promoteWaitingToReservation);
    }

    private void validateExistsReservation(LocalDate reservationDate, Long reservationTimeId, Long themeId) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(reservationDate, reservationTimeId, themeId)) {
            throw new WaitingException("예약이 존재하여 자동 대기 승인이 실패했습니다.");
        }
    }

    private List<Waiting> getWaitings(LocalDate reservationDate, Long reservationTimeId, Long themeId) {
        return waitingRepository.findAllByDateAndTimeIdAndThemeIdOrderByCreatedAtAsc(
                reservationDate,
                reservationTimeId,
                themeId
        );
    }

    private void promoteWaitingToReservation(Waiting waiting) {
        waitingRepository.delete(waiting);
        Reservation reservation = createReservationBy(waiting);
        reservation.validateReservable(LocalDateTime.now(clock));
        reservationRepository.save(reservation);
    }

    private Reservation createReservationBy(Waiting waiting) {
        return new Reservation(
                waiting.getMember(),
                waiting.getDate(),
                waiting.getTime(),
                waiting.getTheme()
        );
    }
}
