package roomescape.service.waiting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.exception.InvalidReservationException;
import roomescape.fixture.*;
import roomescape.service.ServiceTest;
import roomescape.service.waiting.dto.WaitingRequest;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WaitingCreateServiceTest extends ServiceTest {
    @Autowired
    private WaitingCreateService waitingCreateService;

    @DisplayName("사용자가 새로운 예약 대기를 저장한다.")
    @Test
    void createMemberReservation() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest("lini"));
        Member anotherMember = memberRepository.save(MemberFixture.createGuest("pkpk"));
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));
        WaitingRequest waitingRequest = WaitingFixture.createWaitingRequest(theme, reservationDetail);

        //when & then
        assertThatNoException().isThrownBy(() -> waitingCreateService.createWaiting(waitingRequest, anotherMember.getId()));
    }

    @DisplayName("사용자가 이미 예약인 상태에서 예약 대기 요청을 한다면 예외가 발생한다.")
    @Test
    void cannotCreateByExistingMemberWaiting() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));
        WaitingRequest waitingRequest = WaitingFixture.createWaitingRequest(theme, reservationDetail);

        //when & then
        assertThatThrownBy(() -> waitingCreateService.createWaiting(waitingRequest, member.getId()))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이미 예약(대기) 상태입니다.");
    }
}
