package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
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
        log.info("예약 대기 등록 호출 - userId: {}, date: {}, timeId: {}, themeId: {}", user.getId(), date, timeId, themeId);

        TimeSlot timeSlot = getTimeSlotById(timeId);
        Theme theme = getThemeById(themeId);

        validateDuplicateReservation(date, timeSlot.getId(), theme.getId(), user.getId());

        Waiting waiting = waitingRepository.save(Waiting.register(user, date, timeSlot, theme));

        log.info("예약 대기 등록 성공 - id: {}", waiting.getId());
        return waiting;
    }

    @Transactional(readOnly = true)
    public List<Waiting> findAllWaitings() {
        return waitingRepository.findAll();
    }

    @Transactional
    public void removeById(final long id) {
        log.info("예약 대기 삭제 호출 - id: {}", id);
        validateWaitingExists(id);

        waitingRepository.deleteById(id);
        log.info("예약 대기 삭제 성공 - id: {}", id);
    }

    @Transactional
    public void approveNextWaiting(LocalDate date, Long timeSlotId, Long themeId) {
        waitingRepository.findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(date, timeSlotId, themeId).ifPresent(
                nextWaiting -> {
                    PendingPayment approvedReservation = PendingPayment.fromWaiting(nextWaiting);
                    pendingPaymentRepository.save(approvedReservation);
                    waitingRepository.deleteById(nextWaiting.getId());
                    log.info("예약 대기 -> 결제 대기 변경 - 변경된 예약 ID: {}", approvedReservation.getId());
                });
    }

    private void validateDuplicateReservation(
            final LocalDate date, final Long timeSlotId, final Long themeId,
            final Long userId
    ) {
        boolean hasDuplicatedReservation = reservationRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(
                date,
                timeSlotId, themeId, userId
        );

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
