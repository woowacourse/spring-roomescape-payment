package roomescape.domain.reservation;

import roomescape.infrastructure.error.exception.ThemeException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DailyThemeReservations {

    private final List<Reservation> reservations;

    public DailyThemeReservations(final List<Reservation> reservations, final Long themeId, final LocalDate reservationDate) {
        validate(reservations, themeId, reservationDate);
        this.reservations = reservations;
    }

    private void validate(final List<Reservation> reservations, final Long themeId, final LocalDate reservationDate) {
        for (final Reservation reservation : reservations) {
            if (!reservation.isEqualThemeId(themeId) || !reservation.getDate().equals(reservationDate)) {
                throw new ThemeException("특정 테마, 특정 날짜에 속한 예약이 아닙니다.");
            }
        }
    }

    public Set<ReservationTime> calculateBookedTimes() {
        return reservations.stream()
                .map(Reservation::getTime)
                .collect(Collectors.toSet());
    }
}
