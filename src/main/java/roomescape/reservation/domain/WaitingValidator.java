package roomescape.reservation.domain;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.exception.ReservationException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;

@Component
@RequiredArgsConstructor
public class WaitingValidator {

    private final ReservationRepository reservationRepository;
    private final WaitingReservationRepository waitingRepository;

    public void validateCanWaiting(WaitingReservation waiting) {
        validateNotPast(waiting);
        validateSlotEmpty(waiting);
    }

    private void validateNotPast(WaitingReservation waiting) {
        if (waiting.isPast()) {
            throw new ReservationException("지난 날짜와 시간에 대한 대기는 불가능합니다.");
        }
    }

    private void validateSlotEmpty(WaitingReservation waiting) {
        boolean existsBooking = memberHasRegistrationAtSlot(
                waiting.getMember().getId(),
                waiting.getTheme().getId(),
                waiting.getTime().getId(),
                waiting.getDate());
        if (existsBooking) {
            throw new ReservationException("사용자는 이미 해당 날짜에 예약 또는 대기했습니다.");
        }
    }

    private boolean memberHasRegistrationAtSlot(Long memberId, Long themeId, Long timeId, LocalDate date) {
        boolean hasReservation = reservationRepository.memberHasReservationAtSlot(memberId, themeId, timeId, date);
        boolean hasWaiting = waitingRepository.memberHasWaitingAtSlot(memberId, themeId, timeId, date);

        return hasReservation || hasWaiting;
    }

    public void validateCanWaitingApprove(WaitingReservation waitingReservation) {
        if(existsAlreadyInSlot(waitingReservation)) {
            throw new ReservationException("이미 해당 날짜에 예약이 존재합니다.");
        }
    }

    private boolean existsAlreadyInSlot(WaitingReservation waitingReservation) {
        return reservationRepository.existsSameSlot(
                waitingReservation.getDate(),
                waitingReservation.getTime().getId(),
                waitingReservation.getTheme().getId()
        );
    }
}
