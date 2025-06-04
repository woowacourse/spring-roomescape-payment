package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.event.ReservationCancelledEvent;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.pendingpayment.PendingPayment;
import roomescape.domain.reservation.pendingpayment.PendingPaymentRepository;
import roomescape.domain.reservation.waiting.Waiting;
import roomescape.domain.reservation.waiting.WaitingRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.domain.user.User;
import roomescape.exception.AlreadyExistedException;
import roomescape.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final PendingPaymentRepository pendingPaymentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public Waiting saveWaiting(final User user, final LocalDate date, final long timeId, final long themeId) {
        TimeSlot timeSlot = getTimeSlotById(timeId);
        Theme theme = getThemeById(themeId);

        validateDuplicateReservation(date, timeSlot.getId(), theme.getId(), user.getId());

        Waiting waiting = Waiting.register(user, date, timeSlot, theme);
        return waitingRepository.save(waiting);
    }

    @Transactional(readOnly = true)
    public List<Waiting> findAllWaitings() {
        return waitingRepository.findAll();
    }

    @Transactional
    public void removeById(final long id) {
        validateWaitingExists(id);

        waitingRepository.deleteById(id);
    }

    @EventListener
    @Transactional
    public void handleReservationCancelled(ReservationCancelledEvent event) {
        waitingRepository.findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(event.getDate(), event.getTimeSlotId(),
                event.getThemeId()).ifPresent(nextWaiting -> {
            PendingPayment approvedReservation = PendingPayment.fromWaiting(nextWaiting);
            pendingPaymentRepository.save(approvedReservation);
            waitingRepository.deleteById(nextWaiting.getId());
        });
    }

    private void validateDuplicateReservation(final LocalDate date, final Long timeSlotId, final Long themeId,
                                              final Long userId) {
        boolean hasDuplicatedReservation = reservationRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(date,
                timeSlotId, themeId, userId);

        if (hasDuplicatedReservation) {
            throw new AlreadyExistedException("이미 해당 날짜, 시간, 테마에 대한 예약이 존재합니다.");
        }
    }

    private void validateWaitingExists(long id) {
        boolean isWaitingExisted = waitingRepository.existsById(id);

        if (!isWaitingExisted) {
            throw new NotFoundException("존재하지 않는 예약 대기입니다.");
        }
    }

    private TimeSlot getTimeSlotById(final long timeId) {
        return timeSlotRepository.findById(timeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 타임 슬롯입니다."));
    }

    private Theme getThemeById(final long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다."));
    }
}
