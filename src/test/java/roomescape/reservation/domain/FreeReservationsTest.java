package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import roomescape.member.domain.Member;

class FreeReservationsTest {

    @Test
    @DisplayName("무료 예약을 삭제한다.")
    void freeReservationGenerate() {
        // given
        Member member = new Member("aa", "aa@aa.aa", "Aa");
        Theme theme = new Theme("a", "a", "a");
        ReservationTime time = new ReservationTime(LocalTime.of(1, 0));
        Reservation reservation1 = new Reservation(1L, LocalDate.now().plusDays(1), Status.WAITING, member, theme,
                time);
        Reservation reservation2 = new Reservation(2L, LocalDate.now().plusDays(1), Status.WAITING, member, theme,
                time);
        Reservation reservation3 = new Reservation(3L, LocalDate.now().plusDays(1), Status.WAITING, member, theme,
                time);
        Reservation reservation4 = new Reservation(4L, LocalDate.now().plusDays(1), Status.WAITING, member, theme,
                time);

        List<Reservation> allReservations = new ArrayList<>(
                List.of(reservation1, reservation2, reservation3, reservation4));
        List<Reservation> paidReservations = new ArrayList<>(List.of(reservation3, reservation4));

        // when
        FreeReservations freeReservations = new FreeReservations(allReservations, paidReservations);

        // then
        assertThat(freeReservations.getReservations())
                .containsExactlyInAnyOrder(reservation1, reservation2);

    }

}
