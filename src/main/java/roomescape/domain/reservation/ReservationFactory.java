package roomescape.domain.reservation;

import static roomescape.domain.reservation.Status.PAYMENT_PENDING;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.domain.reservation.Status.WAITING;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.exception.reservation.DuplicatedReservationException;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.infrastructure.repository.ReservationTimeRepository;
import roomescape.infrastructure.repository.ThemeRepository;

@Component
@RequiredArgsConstructor
public class ReservationFactory {
    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public Reservation create(long themeId, LocalDate date, long timeId, Member member) {
        Theme theme = themeRepository.getThemeById(themeId);
        ReservationTime time = reservationTimeRepository.getReservationTimeById(timeId);
        validateDuplicateReservation(theme, date, time, member);
        return getReservation(theme, date, time, member);
    }

    private void validateDuplicateReservation(Theme theme, LocalDate date, ReservationTime time, Member member) {
        if (reservationRepository.existsByThemeAndDateAndTimeAndMemberAndStatusIn(theme, date, time, member,
                Status.getStatusWithoutCancel())) {
            throw new DuplicatedReservationException();
        }
    }

    private Reservation getReservation(Theme theme, LocalDate date, ReservationTime time, Member member) {
        if (isAlreadyReserved(theme, date, time)) {
            return new Reservation(member, theme, date, time, WAITING);
        }
        return new Reservation(member, theme, date, time, RESERVED);
    }

    private boolean isAlreadyReserved(Theme theme, LocalDate date, ReservationTime time) {
        return reservationRepository.existsByThemeAndDateAndTimeAndStatusIn(theme, date, time,
                List.of(RESERVED, PAYMENT_PENDING));
    }
}
