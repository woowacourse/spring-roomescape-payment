package roomescape.domain.reservation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.entity.Member;
import roomescape.domain.reservation.dto.AvailableReservationTime;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.reservation.exception.DuplicateReservationExistenceException;
import roomescape.domain.reservation.exception.PastDateException;
import roomescape.domain.reservation.repository.ReservationRepositoryInterface;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.theme.repository.ThemeRepositoryInterface;
import roomescape.domain.time.entity.ReservationTime;
import roomescape.domain.time.repository.ReservationTimeRepositoryInterface;
import roomescape.domain.waiting.exception.DuplicateWaitingExistenceException;
import roomescape.domain.waiting.repository.WaitingRepositoryInterface;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepositoryInterface reservationRepository;
    private final ReservationTimeRepositoryInterface reservationTimeRepository;
    private final ThemeRepositoryInterface themeRepository;
    private final WaitingRepositoryInterface waitingRepository;

    @Transactional
    public Reservation save(final Member member, final LocalDate date, final Long timeId, final Long themeId) {
        validatePastDate(date);

        final ReservationTime reservationTime = reservationTimeRepository.findById(timeId);
        final Theme theme = themeRepository.findById(themeId);

        validateExistReservation(date, reservationTime, theme);
        validateExistWaiting(date, reservationTime, theme);

        final Reservation reservation = new Reservation(member, date, reservationTime, theme);
        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AvailableReservationTime> findAvailableReservationTimes(final LocalDate date, final Long themeId) {
        final List<AvailableReservationTime> availableReservationTimes = new ArrayList<>();
        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        final Theme theme = themeRepository.findById(themeId);

        for (ReservationTime reservationTime : reservationTimes) {
            reservationRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme);
            availableReservationTimes.add(new AvailableReservationTime(
                    reservationTime.getId(),
                    reservationTime.getStartAt(),
                    reservationRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme))
            );
        }

        return availableReservationTimes;
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByMember(final Member member) {
        return reservationRepository.findByMember(member);
    }

    private void validateExistReservation(final LocalDate date, final ReservationTime reservationTime,
                                          final Theme theme) {
        if (reservationRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme)) {
            throw new DuplicateReservationExistenceException("해당 날짜, 시간 그리고 테마에 대한 예약 정보가 존재합니다.");
        }
    }

    private void validateExistWaiting(final LocalDate date, final ReservationTime reservationTime, final Theme theme) {
        if (waitingRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme)) {
            throw new DuplicateWaitingExistenceException("해당 날짜, 시간 그리고 테마에 대한 대기 정보가 존재합니다.");
        }
    }

    private void validatePastDate(final LocalDate date) {
        if (!date.isAfter(LocalDate.now())) {
            throw new PastDateException(date);
        }
    }
}
