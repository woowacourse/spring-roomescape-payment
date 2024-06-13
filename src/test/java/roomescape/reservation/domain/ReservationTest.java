package roomescape.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.vo.Name;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationTest {

    private static final LocalTime TIME = LocalTime.of(9, 0);
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    @Test
    @DisplayName("전달 받은 데이터로 Reservation 객체를 정상적으로 생성한다.")
    void constructReservation() {
        Theme theme = new Theme(1, new Name("미르"), "미르 방탈출", "썸네일 Url", 15000L);
        ReservationTime time = new ReservationTime(1, TIME);
        Member member = new Member(
                1,
                new Name("polla"),
                "polla@gmail.com",
                "polla99",
                MemberRole.ADMIN);
        Reservation reservation = new Reservation(1L, TOMORROW, time, theme, member);

        assertAll(
                () -> assertEquals(theme, reservation.getTheme()),
                () -> assertEquals(time, reservation.getReservationTime()),
                () -> assertEquals(1, reservation.getId()),
                () -> assertEquals("polla", reservation.getMember().getName()),
                () -> assertEquals(TOMORROW, reservation.getDate())
        );
    }
}
