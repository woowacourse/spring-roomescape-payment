package roomescape.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.exception.RoomEscapeBusinessException;

class ReservationTest {

    @DisplayName("예약을 한다.")
    @Test
    void book() {
        // given
        Member member = new Member(1L, "비밥", "uu@naver.com", "1234", Role.USER);
        LocalDate date = LocalDate.parse("2024-06-01");
        ReservationTime time = new ReservationTime(LocalTime.parse("10:00"));
        Theme theme = new Theme("테마이름", "테마 상세", "테마 섬네일");

        Reservation reservation = new Reservation(date, time, theme);

        // when
        BookedMember bookedMember = reservation.book(member);

        // then
        assertThat(bookedMember.isMember(member)).isTrue();
    }

    @DisplayName("이미 예약 되어있으면 예약을 할 수 없다.")
    @Test
    void duplicatedBook() {
        // given
        Member member = new Member(1L, "비밥", "uu@naver.com", "1234", Role.USER);
        LocalDate date = LocalDate.parse("2024-06-01");
        ReservationTime time = new ReservationTime(LocalTime.parse("10:00"));
        Theme theme = new Theme("테마이름", "테마 상세", "테마 섬네일");

        Reservation reservation = new Reservation(date, time, theme);
        reservation.book(member);

        // when // then
        assertThatThrownBy(() -> reservation.book(member))
                .isInstanceOf(RoomEscapeBusinessException.class)
                .hasMessageContaining("이미 예약한 사람이 존재합니다.");
    }

    @DisplayName("이미 예약을 한 사람은 예약 대기를 할 수 없다.")
    @Test
    void addWaitingByDuplicateReservationMember() {
        // given
        Member member = new Member(1L, "비밥", "uu@naver.com", "1234", Role.USER);
        LocalDate date = LocalDate.parse("2024-06-01");
        ReservationTime time = new ReservationTime(LocalTime.parse("10:00"));
        Theme theme = new Theme("테마이름", "테마 상세", "테마 섬네일");

        Reservation reservation = new Reservation(date, time, theme);
        reservation.book(member);

        // when // then
        assertThatThrownBy(() -> reservation.addWaiting(member))
                .isInstanceOf(RoomEscapeBusinessException.class)
                .hasMessage("중복된 예약을 할 수 없습니다.");
    }

    @DisplayName("이미 예약 대기를 한 사람은 예약 대기를 할 수 없다.")
    @Test
    void addWaitingByDuplicateWaitingMember() {
        // given
        Member member = new Member(1L, "비밥", "uu@naver.com", "1234", Role.USER);
        Member member1 = new Member(2L, "비밥1", "uu1@naver.com", "1234", Role.USER);

        LocalDate date = LocalDate.parse("2024-06-01");
        ReservationTime time = new ReservationTime(LocalTime.parse("10:00"));
        Theme theme = new Theme("테마이름", "테마 상세", "테마 섬네일");
        Reservation reservation = new Reservation(date, time, theme);
        reservation.book(member);

        reservation.addWaiting(member1);

        // when // then
        assertThatThrownBy(() -> reservation.addWaiting(member1))
                .isInstanceOf(RoomEscapeBusinessException.class)
                .hasMessage("중복된 예약을 할 수 없습니다.");
    }

    @DisplayName("예약 대기를 한다.")
    @Test
    void addWaiting() {
        // given
        Member member = new Member(1L, "비밥", "uu@naver.com", "1234", Role.USER);
        Member member1 = new Member(2L, "비밥1", "uu1@naver.com", "1234", Role.USER);
        Member member2 = new Member(3L, "비밥3", "uu3@naver.com", "1234", Role.USER);

        LocalDate date = LocalDate.parse("2024-06-01");
        ReservationTime time = new ReservationTime(LocalTime.parse("10:00"));
        Theme theme = new Theme("테마이름", "테마 상세", "테마 섬네일");
        Reservation reservation = new Reservation(date, time, theme);
        reservation.book(member);

        // when
        WaitingMember waitingMember1 = reservation.addWaiting(member1);
        WaitingMember waitingMember2 = reservation.addWaiting(member2);

        // then
        assertAll(
                () -> assertThat(waitingMember1.getMember()).isEqualTo(member1),
                () -> assertThat(waitingMember1.getReservation()).isEqualTo(reservation),
                () -> assertThat(waitingMember2.getMember()).isEqualTo(member2),
                () -> assertThat(waitingMember2.getReservation()).isEqualTo(reservation)
        );
    }

    @DisplayName("예약을 취소한다.")
    @Test
    void approveEmptyWaiting() {
        // given
        Member member = new Member(1L, "비밥", "uu@naver.com", "1234", Role.USER);
        LocalDate date = LocalDate.parse("2024-06-01");
        ReservationTime time = new ReservationTime(LocalTime.parse("10:00"));
        Theme theme = new Theme("테마이름", "테마 상세", "테마 섬네일");
        Reservation reservation = new Reservation(date, time, theme);
        reservation.book(member);

        // when
        reservation.cancelBooked();

        // then
        assertThat(reservation.isBooked()).isFalse();
    }

    @DisplayName("예약을 취소하면 첫 번째 예약 대기자가 예약된다.")
    @Test
    void cancelBooked() {
        // given
        Member member = new Member(1L, "비밥", "uu@naver.com", "1234", Role.USER);
        Member member1 = new Member(2L, "비밥1", "uu1@naver.com", "1234", Role.USER);
        Member member2 = new Member(3L, "비밥3", "uu3@naver.com", "1234", Role.USER);

        LocalDate date = LocalDate.parse("2024-06-01");
        ReservationTime time = new ReservationTime(LocalTime.parse("10:00"));
        Theme theme = new Theme("테마이름", "테마 상세", "테마 섬네일");
        Reservation reservation = new Reservation(date, time, theme);
        reservation.book(member);

        reservation.addWaiting(member1);
        reservation.addWaiting(member2);

        // when
        reservation.cancelBooked();

        // then
        assertAll(
                () -> assertThat(reservation).extracting("bookedMember.member").isEqualTo(member1),
                () -> assertThat(reservation).extracting("waitingMembers").asList()
                        .hasSize(1).extracting("member")
                        .containsExactly(member2)
        );
    }
}
