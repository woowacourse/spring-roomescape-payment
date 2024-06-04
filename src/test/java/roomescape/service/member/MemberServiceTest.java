package roomescape.service.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.fixture.*;
import roomescape.service.ServiceTest;
import roomescape.service.member.dto.MemberReservationResponse;
import roomescape.service.member.dto.MemberResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemberServiceTest extends ServiceTest {
    @Autowired
    private MemberService memberService;

    @DisplayName("존재하는 모든 사용자를 조회한다.")
    @Test
    void findAll() {
        //given
        memberRepository.save(MemberFixture.createGuest());

        //when
        List<MemberResponse> memberResponses = memberService.findAll();

        //then
        assertThat(memberResponses).hasSize(1);
    }

    @DisplayName("id로 사용자를 조회한다.")
    @Test
    void findById() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());

        //when
        Member result = memberService.findById(member.getId());

        //then
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("사용자 본인의 모든 예약 및 대기 정보를 조회한다.")
    @Test
    void findReservations() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest("lini"));
        Member member2 = memberRepository.save(MemberFixture.createGuest("tebah"));
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, ScheduleFixture.createFutureSchedule(1, time)));
        ReservationDetail reservationDetail2 = reservationDetailRepository.save(ReservationDetailFixture.create(theme, ScheduleFixture.createFutureSchedule(2, time)));
        reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));
        reservationRepository.save(WaitingFixture.create(member, reservationDetail2));
        reservationRepository.save(WaitingFixture.create(member2, reservationDetail));

        //when
        List<MemberReservationResponse> result = memberService.findReservations(member.getId());

        //then
        assertThat(result).hasSize(2);
    }
}
