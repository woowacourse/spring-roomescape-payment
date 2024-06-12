package roomescape.service.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.exception.InvalidReservationException;
import roomescape.fixture.*;
import roomescape.service.ServiceTest;
import roomescape.service.reservation.dto.AdminReservationRequest;
import roomescape.service.reservation.dto.ReservationRequest;
import roomescape.service.reservation.dto.ReservationResponse;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReservationCreateServiceTest extends ServiceTest {
    @Autowired
    private ReservationCreateService reservationCreateService;

    @DisplayName("어드민이 새로운 예약을 저장한다.")
    @Test
    void createAdminReservation() {
        //given
        Member admin = memberRepository.save(MemberFixture.createAdmin());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        AdminReservationRequest adminReservationRequest = ReservationFixture.createAdminReservationRequest(admin, reservationDetail);

        //when & then
        assertThatNoException().isThrownBy(() -> reservationCreateService.createAdminReservation(adminReservationRequest));
    }

    @DisplayName("어드민이 새로운 예약을 생성하면 예약 상태로 저장된다.")
    @Test
    void createAdminReservationIsReserved() {
        //given
        Member admin = memberRepository.save(MemberFixture.createAdmin());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        AdminReservationRequest adminReservationRequest = ReservationFixture.createAdminReservationRequest(admin, reservationDetail);

        //when
        ReservationResponse response = reservationCreateService.createAdminReservation(adminReservationRequest);

        //then
        assertThat(response.status()).isEqualTo(ReservationStatus.RESERVED.getDescription());
    }

    @DisplayName("사용자가 새로운 예약을 저장한다.")
    @Test
    void createMemberReservation() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationRequest reservationRequest = ReservationFixture.createReservationRequest(schedule, theme);

        //when & then
        assertThatNoException().isThrownBy(() -> reservationCreateService.createMemberReservation(reservationRequest, member.getId()));
    }

    @DisplayName("사용자가 이미 예약인 상태에서 예약 요청을 한다면 예외가 발생한다.")
    @Test
    void cannotCreateByExistingMemberReservation() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));
        ReservationRequest reservationRequest = ReservationFixture.createReservationRequest(reservationDetail.getSchedule(), reservationDetail.getTheme());

        //when & then
        assertThatThrownBy(() -> reservationCreateService.createMemberReservation(reservationRequest, member.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이미 예약(대기)가 존재하여 예약이 불가능합니다.");
    }

    @DisplayName("사용자가 이미 예약 대기인 상태에서 예약 요청을 한다면 예외가 발생한다.")
    @Test
    void cannotCreateByExistingMemberWaiting() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(WaitingFixture.create(member, reservationDetail));
        ReservationRequest reservationRequest = ReservationFixture.createReservationRequest(reservationDetail.getSchedule(), reservationDetail.getTheme());

        //when & then
        assertThatThrownBy(() -> reservationCreateService.createMemberReservation(reservationRequest, member.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이미 예약(대기)가 존재하여 예약이 불가능합니다.");
    }

    @DisplayName("사용자가 이미 결제 대기인 상태에서 예약 요청을 한다면 예외가 발생한다.")
    @Test
    void cannotCreateByExistingMemberPendingPayment() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(ReservationFixture.createPendingPayment(member, reservationDetail));
        ReservationRequest reservationRequest = ReservationFixture.createReservationRequest(reservationDetail.getSchedule(), reservationDetail.getTheme());

        //when & then
        assertThatThrownBy(() -> reservationCreateService.createMemberReservation(reservationRequest, member.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이미 예약(대기)가 존재하여 예약이 불가능합니다.");
    }

    @DisplayName("존재하지 않는 시간으로 예약을 추가하면 예외를 발생시킨다.")
    @Test
    void cannotCreateByUnknownTime() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        Long invalidTimeId = 0L;
        LocalDate date = LocalDate.now();
        ReservationRequest reservationRequest = ReservationFixture.createReservationRequest(date, invalidTimeId, theme);

        //when & then
        assertThatThrownBy(() -> reservationCreateService.createMemberReservation(reservationRequest, member.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("더이상 존재하지 않는 시간입니다.");
    }

    @DisplayName("존재하지 않는 테마로 예약을 추가하면 예외를 발생시킨다.")
    @Test
    void cannotCreateByUnknownTheme() {
        //given
        Member guest = memberRepository.save(MemberFixture.createGuest());
        Long invalidThemeId = 0L;
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationRequest reservationRequest = ReservationFixture.createReservationRequest(schedule, invalidThemeId);

        //when & then
        assertThatThrownBy(() -> reservationCreateService.createMemberReservation(reservationRequest, guest.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("더이상 존재하지 않는 테마입니다.");
    }

}
