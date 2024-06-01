package roomescape.domain.reservation;

import static roomescape.domain.reservation.Status.PAYMENT_PENDING;
import static roomescape.domain.reservation.Status.RESERVED;
import static roomescape.domain.reservation.Status.WAITING;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.exception.reservation.DuplicatedReservationException;

@Component
@RequiredArgsConstructor
public class ReservationFactory {
    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public Reservation create(long themeId, LocalDate date, long timeId, Member member) {
        Theme theme = themeRepository.getById(themeId);
        ReservationTime time = reservationTimeRepository.getReservationTime(timeId);
        validatePastTime(date, time);
        validateDuplicateReservation(theme, date, time, member);
        return getReservation(theme, date, time, member);
    }

    private void validateDuplicateReservation(Theme theme, LocalDate date, ReservationTime time, Member member) {
        if (reservationRepository.existsReservation(theme, date, time, member, Status.getStatusWithoutCancel())) {
            throw new DuplicatedReservationException();
        }
    }

    private Reservation getReservation(Theme theme, LocalDate date, ReservationTime time, Member member) {
        if (reservationRepository.existsReservation(theme, date, time, List.of(RESERVED, PAYMENT_PENDING))) {
            return new Reservation(member, theme, date, time, WAITING);
        }
        return new Reservation(member, theme, date, time, RESERVED);
    }

    private void validatePastTime(LocalDate date, ReservationTime time) {
        if (isBefore(date, time)) {
            throw new IllegalArgumentException(String.format("이미 지난 시간입니다. 입력한 예약 시간: %s", time));
        }
    }

    private boolean isBefore(LocalDate date, ReservationTime time) {
        return LocalDateTime.of(date, time.getStartAt())
                .isBefore(LocalDateTime.now());
    }
}
