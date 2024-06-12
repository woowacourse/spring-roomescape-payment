package roomescape.service.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.dto.ReservationWithRank;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InvalidReservationException;
import roomescape.fixture.*;
import roomescape.service.ServiceTest;
import roomescape.service.reservation.dto.ReservationConfirmRequest;
import roomescape.service.reservation.dto.ReservationConfirmedResponse;
import roomescape.service.reservation.dto.ReservationFilterRequest;
import roomescape.service.reservation.dto.ReservationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationCommonServiceTest extends ServiceTest {

    @Autowired
    private ReservationCommonService reservationCommonService;

    @DisplayName("모든 예약 내역을 조회한다.")
    @Test
    void findAllReservations() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));

        //when
        List<ReservationResponse> reservations = reservationCommonService.findAll();

        //then
        assertThat(reservations).hasSize(1);
    }

    @DisplayName("특정 조건으로 예약 내역을 조회한다.")
    @Test
    void findByMember() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));
        ReservationFilterRequest reservationFilterRequest = new ReservationFilterRequest(member.getId(), null, null, null);

        //then
        assertThatNoException().isThrownBy(() -> reservationCommonService.findByCondition(reservationFilterRequest));
    }

    @DisplayName("id로 예약 결제를 취소 후 삭제한다.")
    @Test
    void deleteReservationById() {
        //given
        Member admin = memberRepository.save(MemberFixture.createAdmin());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        Reservation reservation = reservationRepository.save(ReservationFixture.createReserved(admin, reservationDetail));

        //when
        assertThatNoException().isThrownBy(() -> reservationRepository.deleteById(reservation.getId()));
    }

    @DisplayName("예약을 삭제하고, 예약 대기가 있다면 가장 우선순위가 높은 예약 대기를 결제 대기로 전환한다.")
    @Test
    void deleteThenUpdateReservation() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Member anotherMember = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        Payment payment = paymentRepository.save(PaymentFixture.create());
        Reservation reservation = reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail, payment));
        reservationRepository.save(WaitingFixture.create(anotherMember, reservationDetail));

        //when
        reservationCommonService.deleteById(reservation.getId());

        //then
        List<Boolean> reservations = reservationRepository.findWithRankingByMemberId(anotherMember.getId()).stream()
                .map(ReservationWithRank::reservation)
                .map(Reservation::isPendingPayment)
                .toList();
        assertThat(reservations).containsExactly(true);
    }

    @DisplayName("과거 예약을 삭제하려고 하면 예외가 발생한다.")
    @Test
    @Sql({"/truncate.sql", "/member.sql", "/theme.sql", "/time.sql", "/reservation-past-detail.sql", "/payment.sql", "/reservation.sql"})
    void cannotDeleteReservationByIdIfPast() {
        //given
        long pastReservationId = 1;

        //when & then
        assertThatThrownBy(() -> reservationCommonService.deleteById(pastReservationId))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이미 지난 예약은 삭제할 수 없습니다.");
    }

    @DisplayName("결제 대기 상태의 예약을 결제 후, 예약 상태로 변경한다.")
    @Test
    void confirmReservation() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        Reservation reservation = reservationRepository.save(ReservationFixture.createPendingPayment(member, reservationDetail));
        ReservationConfirmRequest reservationConfirmRequest = ReservationFixture.createReservationConfirmRequest(reservation);

        //when
        ReservationConfirmedResponse response = reservationCommonService.confirmReservation(reservationConfirmRequest, member.getId());

        //then
        assertAll(
                () -> assertThat(response.payment().paymentKey()).isEqualTo(reservationConfirmRequest.paymentRequest().paymentKey()),
                () -> assertThat(response.status()).isEqualTo(ReservationStatus.RESERVED.getDescription())
        );
    }

    @DisplayName("존재하지 않는 결제 대기 정보에 대해 결제를 할 수 없다.")
    @Test
    void cannotConfirmReservationOfUnknownReservation() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        long unknownId = 0;
        ReservationConfirmRequest reservationConfirmRequest = new ReservationConfirmRequest(unknownId, PaymentFixture.createPaymentRequest());

        //when & then
        assertThatThrownBy(() -> reservationCommonService.confirmReservation(reservationConfirmRequest, member.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("더이상 존재하지 않는 결제 대기 정보입니다.");
    }

    @DisplayName("본인의 결제 대기가 아닌 경우 결제할 수 없다.")
    @Test
    void cannotConfirmReservationByNotOwner() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Member anotherMember = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        Reservation reservation = reservationRepository.save(ReservationFixture.createPendingPayment(member, reservationDetail));
        ReservationConfirmRequest reservationConfirmRequest = ReservationFixture.createReservationConfirmRequest(reservation);

        //when & then
        assertThatThrownBy(() -> reservationCommonService.confirmReservation(reservationConfirmRequest, anotherMember.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("본인의 예약만 결제할 수 있습니다.");
    }

    @DisplayName("예약 대기에 대해 결제를 진행하려고 하면 예외가 발생한다.")
    @Test
    void cannotConfirmReservationOfWaiting() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        Reservation reservation = reservationRepository.save(WaitingFixture.create(member, reservationDetail));
        ReservationConfirmRequest reservationConfirmRequest = ReservationFixture.createReservationConfirmRequest(reservation);

        //when & then
        assertThatThrownBy(() -> reservationCommonService.confirmReservation(reservationConfirmRequest, member.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("결재 대기 상태에서만 결재 가능합니다.");
    }

    @DisplayName("예약에 대해 결제를 진행하려고 하면 예외가 발생한다.")
    @Test
    void cannotConfirmReservationOfReserved() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        Reservation reservation = reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));
        ReservationConfirmRequest reservationConfirmRequest = ReservationFixture.createReservationConfirmRequest(reservation);

        //when & then
        assertThatThrownBy(() -> reservationCommonService.confirmReservation(reservationConfirmRequest, member.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("결재 대기 상태에서만 결재 가능합니다.");
    }
}
