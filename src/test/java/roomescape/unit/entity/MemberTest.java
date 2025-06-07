package roomescape.unit.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import roomescape.entity.Member;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;
import roomescape.exception.custom.InvalidReservationException;
import roomescape.global.ReservationStatus;
import roomescape.global.Role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberTest {

    @Test
    void 예약을_생성하고_추가한다() {
        // given
        Member member = new Member(1L, "flint", "test@test.com", "test", Role.USER);
        LocalDate date = LocalDate.now().plusDays(1);
        ReservationTime time = new ReservationTime(1L, LocalTime.of(14, 0));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");
        ReservationStatus status = ReservationStatus.RESERVED;

        // when
        Reservation reservation = member.reserve(date, time, theme, status);

        // then
        Assertions.assertAll(
                () -> assertThat(reservation.getMember()).isEqualTo(member),
                () -> assertThat(reservation.getDate()).isEqualTo(date),
                () -> assertThat(reservation.getReservationTime()).isEqualTo(time),
                () -> assertThat(reservation.getTheme()).isEqualTo(theme),
                () -> assertThat(reservation.getStatus()).isEqualTo(status),
                () -> assertThat(member.getReservations()).hasSize(1)
        );
    }

    @Test
    void 중복된_예약을_생성하면_예외가_발생한다() {
        // given
        Member member = new Member(1L, "flint", "test@test.com", "test", Role.USER);
        LocalDate date = LocalDate.now().plusDays(1);
        ReservationTime time = new ReservationTime(1L, LocalTime.of(14, 0));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");
        ReservationStatus status = ReservationStatus.RESERVED;
        
        member.reserve(date, time, theme, status);

        // when & then
        assertThatThrownBy(() -> member.reserve(date, time, theme, status))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessageContaining("중복된 예약신청입니다");
    }

    @Test
    void 과거_날짜로_예약하면_예외가_발생한다() {
        // given
        Member member = new Member("flint", "test@test.com", "test", Role.USER);
        LocalDate pastDate = LocalDate.now().minusDays(1);
        ReservationTime time = new ReservationTime(1L, LocalTime.of(14, 0));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");
        ReservationStatus status = ReservationStatus.RESERVED;

        // when & then
        assertThatThrownBy(() -> member.reserve(pastDate, time, theme, status))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessageContaining("과거 날짜 및 시간으로 예약할 수 없습니다");
    }

    @Test
    void 대기_상태를_결제대기_상태로_변경한다() {
        // given
        Member member = new Member("flint", "test@test.com", "test", Role.USER);
        LocalDate date = LocalDate.now().plusDays(1);
        ReservationTime time = new ReservationTime(1L, LocalTime.of(14, 0));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        member.reserve(date, time, theme, ReservationStatus.WAIT);

        // when
        member.waitToPending(date, time, theme);

        // then
        Reservation reservation = member.getReservations().getFirst();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void 대기중인_예약이_없으면_예외가_발생한다() {
        // given
        Member member = new Member("flint", "test@test.com", "test", Role.USER);
        LocalDate date = LocalDate.now().plusDays(1);
        ReservationTime time = new ReservationTime(1L, LocalTime.of(14, 0));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        member.reserve(date, time, theme, ReservationStatus.RESERVED);

        // when & then
        assertThatThrownBy(() -> member.waitToPending(date, time, theme))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessageContaining("대기중인 예약이 없습니다");
    }
}
