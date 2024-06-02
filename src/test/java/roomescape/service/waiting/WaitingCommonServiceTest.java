package roomescape.service.waiting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InvalidReservationException;
import roomescape.fixture.*;
import roomescape.service.ServiceTest;
import roomescape.service.reservation.dto.ReservationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WaitingCommonServiceTest extends ServiceTest {
    @Autowired
    private WaitingCommonService waitingCommonService;

    @DisplayName("모든 예약 대기 내역을 조회한다.")
    @Test
    void findAllWaitings() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest("lini"));
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(WaitingFixture.create(member, reservationDetail));

        //when
        List<ReservationResponse> reservations = waitingCommonService.findAll();

        //then
        assertThat(reservations).hasSize(1);
    }


    @DisplayName("예약을 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteWaitingByIdIfReserved() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest("lini"));
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        Reservation reservation = reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));

        //when
        assertThatThrownBy(() -> waitingCommonService.deleteWaitingById(reservation.getId(), member.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("예약은 삭제할 수 없습니다. 관리자에게 문의해주세요.");
    }

    @DisplayName("사용자가 본인 외 예약 대기를 삭제하려고 하면 예외가 발생한다.")
    @Test
    void cannotDeleteWaitingByIdIfNotOwner() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest("lini"));
        Member anotherMember = memberRepository.save(MemberFixture.createGuest("pkpk"));
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        Reservation reservation = reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));

        //when
        assertThatThrownBy(() -> waitingCommonService.deleteWaitingById(reservation.getId(), anotherMember.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("예약 대기를 삭제할 권한이 없습니다.");
    }
}
