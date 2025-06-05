package roomescape.domain.reservation.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import roomescape.domain.member.Role;
import roomescape.domain.member.entity.Member;
import roomescape.domain.reservation.exception.ReservationException;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.time.entity.ReservationTime;

class ReservationTest {

    @Test
    void 날짜가_null이면_예외가_발생한다() {
        // given
        final Member member = new Member(1L, "이스트", "east@email.com", "1234", Role.ADMIN);
        final LocalDate date = null;
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        final Theme theme = new Theme("헤일러", "헤일러 설명", "헤일러 썸네일");

        // when & then
        Assertions.assertThatThrownBy(() -> {
            new Reservation(member, date, reservationTime, theme);
        }).isInstanceOf(ReservationException.class);
    }

    @Test
    void 예약_시간이_null이면_예외가_발생한다() {
        // given
        final Member member = new Member(1L, "이스트", "east@email.com", "1234", Role.ADMIN);
        final LocalDate date = LocalDate.of(2025, 4, 24);
        final ReservationTime reservationTime = null;
        final Theme theme = new Theme("헤일러", "헤일러 설명", "헤일러 썸네일");

        // when & then
        Assertions.assertThatThrownBy(() -> {
            new Reservation(member, date, reservationTime, theme);
        }).isInstanceOf(ReservationException.class);
    }

    @Test
    void 테마가_null이면_예외가_발생한다() {
        // given
        final Member member = new Member(1L, "이스트", "east@email.com", "1234", Role.ADMIN);
        final LocalDate date = LocalDate.of(2025, 4, 24);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        final Theme theme = null;

        // when & then
        Assertions.assertThatThrownBy(() -> {
            new Reservation(member, date, reservationTime, theme);
        }).isInstanceOf(ReservationException.class);
    }

    @Test
    void 멤버가_null이면_예외가_발생한다() {
        // given
        final Member member = null;
        final LocalDate date = LocalDate.of(2025, 4, 24);
        final ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        final Theme theme = new Theme("헤일러", "헤일러 설명", "헤일러 썸네일");

        // when & then
        Assertions.assertThatThrownBy(() -> {
            new Reservation(member, date, reservationTime, theme);
        }).isInstanceOf(ReservationException.class);
    }
}
