package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.domain.user.User;
import roomescape.domain.waiting.Waiting;
import roomescape.domain.waiting.WaitingRepository;
import roomescape.exception.AlreadyExistedException;
import roomescape.exception.BusinessRuleViolationException;
import roomescape.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ThemeRepository themeRepository;

    @Transactional
    public Waiting saveWaiting(final User user,
                               final LocalDate date,
                               final long timeId,
                               final long themeId) {
        TimeSlot timeSlot = getTimeSlotById(timeId);
        Theme theme = getThemeById(themeId);

        validateDuplicateWaiting(date, timeSlot.getId(), theme.getId(), user.getId());
        validateNotAlreadyReserved(date, timeSlot.getId(), theme.getId(), user.getId());

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

    private void validateDuplicateWaiting(final LocalDate date,
                                          final long timeSlotId,
                                          final long themeId,
                                          final long userId) {
        boolean isWaitingExisted =
                waitingRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlotId, themeId, userId);

        if (isWaitingExisted) {
            throw new AlreadyExistedException("이미 예약 대기한 내역이 있습니다.");
        }
    }

    private void validateNotAlreadyReserved(final LocalDate date,
                                            final long timeSlotId,
                                            final long themeId,
                                            final long userId) {
        boolean isAlreadyReserved =
                reservationRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlotId, themeId, userId);

        if (isAlreadyReserved) {
            throw new BusinessRuleViolationException("해당 테마의 시간대에 이미 예약되어 있습니다.");
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
