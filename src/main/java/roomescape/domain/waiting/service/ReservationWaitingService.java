package roomescape.domain.waiting.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.entity.Member;
import roomescape.domain.reservation.exception.PastDateException;
import roomescape.domain.reservation.repository.ReservationRepositoryInterface;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.theme.repository.ThemeRepositoryInterface;
import roomescape.domain.time.entity.ReservationTime;
import roomescape.domain.time.repository.ReservationTimeRepositoryInterface;
import roomescape.domain.waiting.entity.Waiting;
import roomescape.domain.waiting.exception.WaitingNotAllowedException;
import roomescape.domain.waiting.exception.WaitingNotFoundException;
import roomescape.domain.waiting.repository.WaitingRepositoryInterface;

@RequiredArgsConstructor
@Service
public class ReservationWaitingService {

    private static final int WAITING_COUNT = 1;

    private final ReservationRepositoryInterface reservationRepository;
    private final ReservationTimeRepositoryInterface reservationTimeRepository;
    private final ThemeRepositoryInterface themeRepository;
    private final WaitingRepositoryInterface waitingRepository;

    @Transactional
    public Waiting createWaitingReservation(
            final Member member,
            final LocalDate date,
            final Long timeId,
            final Long themeId) {

        validatePastDate(date);

        final ReservationTime reservationTime = reservationTimeRepository.findById(timeId);
        final Theme theme = themeRepository.findById(themeId);
        final boolean reservationExists = reservationRepository.existsByDateAndTimeAndTheme(date, reservationTime,
                theme);

        if (reservationExists) {
            final Waiting waiting = new Waiting(member, reservationTime, theme, date);
            return waitingRepository.save(waiting);
        }

        throw new WaitingNotAllowedException("예약이 존재하지 않아 대기열에 등록 할 수 없습니다.");
    }

    @Transactional
    public void deleteWaitingById(final Long id) {
        waitingRepository.findById(id)
                .ifPresentOrElse(
                        waiting -> waitingRepository.deleteById(id),
                        () -> {
                            throw new WaitingNotFoundException("해당 대기 데이터가 존재하지 않습니다. id = " + id);
                        }
                );
    }

    @Transactional(readOnly = true)
    public List<Waiting> findWaitingByMember(final Member member) {
        return waitingRepository.findByMember(member);
    }

    @Transactional(readOnly = true)
    public long getRankInWaiting(final Waiting waiting) {
        return waitingRepository.countBefore(
                waiting.getTheme(),
                waiting.getDate(),
                waiting.getTime(),
                waiting.getId()
        ) + WAITING_COUNT;
    }

    private void validatePastDate(final LocalDate date) {
        if (!date.isAfter(LocalDate.now())) {
            throw new PastDateException(date);
        }
    }
}
