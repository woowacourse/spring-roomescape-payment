package roomescape.domain.reservation;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.support.fixture.MemberFixture.MEMBER_SUN;
import static roomescape.support.fixture.ThemeFixture.THEME_BED;
import static roomescape.support.fixture.TimeFixture.ONE_PM;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.exception.member.AuthenticationFailureException;
import roomescape.exception.reservation.CancelReservationException;

class ReservationTest {

    Member member = MEMBER_SUN.create();
    Theme theme = THEME_BED.create();
    LocalDate date = LocalDate.now().plusDays(1);
    ReservationTime time = ONE_PM.create();

    @DisplayName("예약 상태를 PAYMENT_PENDING 으로 변경할 시 현재 상태가 CANCELED 라면 예외를 발생시킨다. ")
    @Test
    void throw_exception_when_to_pending_and_current_status_canceled() {
        Reservation reservation = new Reservation(member, theme, date, time, Status.CANCELED);

        assertThatThrownBy(reservation::toPending)
                .isInstanceOf(CancelReservationException.class);
    }

    @DisplayName("정상적으로 예약 상태를 PAYMENT_PANDING으로 변경한다.")
    @Test
    void success_to_pending() {
        Reservation reservation = new Reservation(member, theme, date, time, Status.WAITING);

        assertThatNoException()
                .isThrownBy(reservation::toPending);
    }

    @DisplayName("예약 상태를 RESERVED 로 변경할 시 현재 상태가 CANCELED라면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_to_reserved_and_current_status_canceled() {
        Reservation reservation = new Reservation(member, theme, date, time, Status.CANCELED);

        assertThatThrownBy(reservation::toReserved)
                .isInstanceOf(CancelReservationException.class);
    }

    @DisplayName("정상적으로 예약 상태를 RESERVED로 변경한다.")
    @Test
    void success_to_reserved() {
        Reservation reservation = new Reservation(member, theme, date, time, Status.WAITING);

        assertThatNoException()
                .isThrownBy(reservation::toReserved);
    }

    @DisplayName("예약 상태를 CANCELED로 변경할 시 예약의 회원 id가 일치하지 않으면 예외를 발생시킨다.")
    @Test
    void throw_exception_when_changing_to_canceled_and_member_id_does_not_match() {
        Reservation reservation = new Reservation(member, theme, date, time, Status.CANCELED);

        assertThatThrownBy(() -> reservation.validateOwner(2L))
                .isInstanceOf(AuthenticationFailureException.class);
    }

    @DisplayName("정상적으로 예약 상태를 CANCELED로 변경한다.")
    @Test
    void success_to_canceled() {
        Reservation reservation = new Reservation(member, theme, date, time, Status.WAITING);

        assertThatNoException()
                .isThrownBy(reservation::cancel);
    }

    @DisplayName("예약의 회원 id와 일치하지 않으면 예외를 발생시킨다.")
    @Test
    void throw_exception_if_reservation_member_id_does_not_match() {
        Reservation reservation = new Reservation(member, theme, date, time, Status.CANCELED);

        assertThatThrownBy(() -> reservation.validateOwner(2L))
                .isInstanceOf(AuthenticationFailureException.class);
    }
}
