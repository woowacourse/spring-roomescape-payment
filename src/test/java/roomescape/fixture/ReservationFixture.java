package roomescape.fixture;

import static roomescape.fixture.MemberFixture.memberFixture;
import static roomescape.fixture.PaymentFixture.paymentFixture;
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
                    .member(memberFixture(1L))
                    .date(LocalDate.parse("2025-06-01"))
                    .time(timeFixture(1L))
                    .theme(themeFixture(1L))
                    .status(ReservationStatus.RESERVED)
                    .payment(paymentFixture(1L))
                    .build(),
            Reservation.builder()
                    .id(2L)
                    .member(memberFixture(2L))
                    .date(LocalDate.parse("2025-06-02"))
                    .time(timeFixture(2L))
                    .theme(themeFixture(2L))
                    .status(ReservationStatus.RESERVED)
                    .payment(paymentFixture(2L))
                    .build(),
            Reservation.builder()
                    .id(3L)
                    .member(memberFixture(3L))
                    .date(LocalDate.parse("2025-06-03"))
                    .time(timeFixture(3L))
                    .theme(themeFixture(3L))
                    .status(ReservationStatus.RESERVED)
                    .payment(paymentFixture(3L))
                    .build(),
            Reservation.builder()
                    .id(4L)
                    .member(memberFixture(4L))
                    .date(LocalDate.parse("2025-06-04"))
                    .time(timeFixture(4L))
                    .theme(themeFixture(4L))
                    .status(ReservationStatus.RESERVED)
                    .payment(paymentFixture(4L))
                    .build()
    );

    public static final List<Reservation> WAITINGS = List.of(
            Reservation.builder()
                    .id(1L)
                    .member(memberFixture(1L))
                    .date(LocalDate.parse("2025-06-01"))
                    .time(timeFixture(1L))
                    .theme(themeFixture(1L))
                    .status(ReservationStatus.PENDING)
                    .build(),
            Reservation.builder()
                    .id(2L)
                    .member(memberFixture(2L))
                    .date(LocalDate.parse("2025-06-02"))
                    .time(timeFixture(2L))
                    .theme(themeFixture(2L))
                    .status(ReservationStatus.PENDING)
                    .build(),
            Reservation.builder()
                    .id(3L)
                    .member(memberFixture(3L))
                    .date(LocalDate.parse("2025-06-03"))
                    .time(timeFixture(3L))
                    .theme(themeFixture(3L))
                    .status(ReservationStatus.PENDING)
                    .build(),
            Reservation.builder()
                    .id(4L)
                    .member(memberFixture(4L))
                    .date(LocalDate.parse("2025-06-04"))
                    .time(timeFixture(4L))
                    .theme(themeFixture(4L))
                    .status(ReservationStatus.PENDING)
                    .build()
    );

    public static Reservation reservationFixture(long id) {
        assert id <= RESERVATIONS.size();
        return RESERVATIONS.get((int) (id - 1));
    }

    public static Reservation waitingFixture(long id) {
        assert id <= WAITINGS.size();
        return WAITINGS.get((int) (id - 1));
    }
}
