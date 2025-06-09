package roomescape.unit.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import roomescape.entity.Member;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.entity.Theme;
import roomescape.exception.custom.InvalidReservationException;
import roomescape.global.ReservationStatus;
import roomescape.global.Role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReservationTest {

    @Test
    void 현재_날짜보다_과거_날짜이면_false를_반환한다() {
        //given
        Member member = new Member(0L, "Hula", "test@test.com", "test", Role.USER);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now());
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        Reservation reservation = new Reservation(member, yesterday, reservationTime, theme,
                ReservationStatus.RESERVED);

        //when
        boolean actual = reservation.isBefore(LocalDateTime.now());

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void 현재_시간보다_과거_시간이면_false를_반환한다() {
        //given
        Member member = new Member(0L, "Hula", "test@test.com", "test", Role.USER);
        LocalDate today = LocalDate.now();
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now().minusMinutes(10));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        Reservation reservation = new Reservation(member, today, reservationTime, theme, ReservationStatus.RESERVED);

        //when
        boolean actual = reservation.isBefore(LocalDateTime.now());

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void 취소_처리_시_상태를_취소로_변경한다() {
        //given
        Member member = new Member(1L, "Hula", "test@test.com", "test", Role.USER);
        LocalDate today = LocalDate.now();
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now().minusMinutes(10));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        Reservation reservation = new Reservation(member, today, reservationTime, theme, ReservationStatus.RESERVED);

        //when
        reservation.cancel();

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    void 대기_상태를_결제대기_상태로_변경한다() {
        //given
        Member member = new Member(1L, "Hula", "test@test.com", "test", Role.USER);
        LocalDate today = LocalDate.now();
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now().minusMinutes(10));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        Reservation reservation = new Reservation(member, today, reservationTime, theme, ReservationStatus.WAIT);

        //when
        reservation.waitToPending();

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.class, mode = Mode.EXCLUDE, names = {"WAIT"})
    void 대기_상태가_아닌_예약은_결제대기_상태로_변경할_수_없다(ReservationStatus status) {
        //given
        Member member = new Member(1L, "Hula", "test@test.com", "test", Role.USER);
        LocalDate today = LocalDate.now();
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now().minusMinutes(10));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        Reservation reservation = new Reservation(member, today, reservationTime, theme, status);

        //when & then
        assertThatThrownBy(reservation::waitToPending)
                .isInstanceOf(InvalidReservationException.class)
                .hasMessageContaining("대기 중인 예약이 아닙니다.");
    }

    @Test
    void member_필드가_null인_예약은_결제대기_상태로_변경할_수_없다() {
        //given
        LocalDate today = LocalDate.now();
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now().minusMinutes(10));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        Reservation reservation = new Reservation(null, today, reservationTime, theme, ReservationStatus.WAIT);

        //when & then
        assertThatThrownBy(reservation::waitToPending)
                .isInstanceOf(InvalidReservationException.class)
                .hasMessageContaining("Member 가 없는 대기 reservation은 결제 대기로 변경할 수 없습니다.");
    }

    @Test
    void 결제_대기_상태를_예약_상태로_변경한다() {
        //given
        Member member = new Member(1L, "Hula", "test@test.com", "test", Role.USER);
        LocalDate today = LocalDate.now();
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now().minusMinutes(10));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        Reservation reservation = new Reservation(member, today, reservationTime, theme, ReservationStatus.PENDING);

        //when
        reservation.pendingToReserve();

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatus.class, mode = Mode.EXCLUDE, names = {"PENDING"})
    void 결제_대기_상태가_아닌_예약은_예약_상태로_변경할_수_없다(ReservationStatus status) {
        //given
        Member member = new Member(1L, "Hula", "test@test.com", "test", Role.USER);
        LocalDate today = LocalDate.now();
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now().minusMinutes(10));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        Reservation reservation = new Reservation(member, today, reservationTime, theme, status);

        //when & then
        assertThatThrownBy(reservation::pendingToReserve)
                .isInstanceOf(InvalidReservationException.class)
                .hasMessageContaining("결제 대기중인 예약이 아닙니다.");
    }

    @Test
    void 예약의_결제_정보를_설정한다() {
        //given
        Member member = new Member(1L, "Hula", "test@test.com", "test", Role.USER);
        LocalDate today = LocalDate.now();
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.now().minusMinutes(10));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");

        Reservation reservation = new Reservation(member, today, reservationTime, theme, ReservationStatus.RESERVED);

        Payment payment = new Payment(1L, "orderId", "paymentKey", 1000, null);

        //when
        reservation.payForReservation(payment);

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }
}
