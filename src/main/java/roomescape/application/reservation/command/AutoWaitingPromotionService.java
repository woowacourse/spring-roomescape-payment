package roomescape.application.reservation.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.WaitingRepository;
import roomescape.infrastructure.error.exception.WaitingException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AutoWaitingPromotionService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    public AutoWaitingPromotionService(final WaitingRepository waitingRepository,
                                       final ReservationRepository reservationRepository,
                                       final Clock clock) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
    }

    public void promote(final LocalDate reservationDate, final Long reservationTimeId, final Long themeId) {
        validateExistsReservation(reservationDate, reservationTimeId, themeId);
        final List<Waiting> waitings = getWaitings(reservationDate, reservationTimeId, themeId);
        if (waitings.isEmpty()) {
            return;
        }
        waitings.stream()
                .findFirst()
                .ifPresent(this::promoteWaitingToReservation);
    }

    private void validateExistsReservation(final LocalDate reservationDate, final Long reservationTimeId, final Long themeId) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(reservationDate, reservationTimeId, themeId)) {
            throw new WaitingException("예약이 존재하여 자동 대기 승인이 실패했습니다.");
        }
    }

    private List<Waiting> getWaitings(final LocalDate reservationDate, final Long reservationTimeId, final Long themeId) {
        return waitingRepository.findAllByDateAndTimeIdAndThemeIdOrderByCreatedAtAsc(
                reservationDate,
                reservationTimeId,
                themeId
        );
    }

    private void promoteWaitingToReservation(final Waiting waiting) {
        waitingRepository.delete(waiting);
        final Reservation reservation = createReservationBy(waiting);
        reservation.validateReservable(LocalDateTime.now(clock));
        reservationRepository.save(reservation);
    }

    private Reservation createReservationBy(final Waiting waiting) {
        return new Reservation(
                waiting.getMember(),
                waiting.getDate(),
                waiting.getTime(),
                waiting.getTheme()
        );
    }
}
