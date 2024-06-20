package roomescape.fixture;

import static roomescape.fixture.MemberFixture.memberFixture;
import static roomescape.fixture.ThemeFixture.themeFixture;
import static roomescape.fixture.TimeFixture.timeFixture;

import java.time.LocalDate;
import java.util.List;

import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;

public class ReservationFixture {

    public static final List<Reservation> RESERVATIONS = List.of(
            Reservation.builder()
                    .id(1L)
                    .member(memberFixture(1))
                    .date(LocalDate.parse("2025-06-01"))
                    .time(timeFixture(1))
                    .theme(themeFixture(1))
                    .status(ReservationStatus.RESERVED)
                    .build(),
            Reservation.builder()
                    .id(2L)
                    .member(memberFixture(2))
                    .date(LocalDate.parse("2025-06-02"))
                    .time(timeFixture(2))
                    .theme(themeFixture(2))
                    .status(ReservationStatus.PENDING)
                    .build(),
            Reservation.builder()
                    .id(3L)
                    .member(memberFixture(3))
                    .date(LocalDate.parse("2025-06-03"))
                    .time(timeFixture(3))
                    .theme(themeFixture(3))
                    .status(ReservationStatus.RESERVED)
                    .build(),
            Reservation.builder()
                    .id(4L)
                    .member(memberFixture(4))
                    .date(LocalDate.parse("2025-06-04"))
                    .time(timeFixture(4))
                    .theme(themeFixture(4))
                    .status(ReservationStatus.PENDING)
                    .build()
    );

    public static Reservation reservationFixture(int id) {
        return RESERVATIONS.get(id - 1);
    }
}
