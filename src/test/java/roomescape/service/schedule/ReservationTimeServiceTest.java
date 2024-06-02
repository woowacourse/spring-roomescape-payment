package roomescape.service.schedule;

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
import roomescape.service.schedule.dto.AvailableReservationTimeResponse;
import roomescape.service.schedule.dto.ReservationTimeCreateRequest;
import roomescape.service.schedule.dto.ReservationTimeReadRequest;
import roomescape.service.schedule.dto.ReservationTimeResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationTimeServiceTest extends ServiceTest {
    @Autowired
    private ReservationTimeService reservationTimeService;

    @DisplayName("새로운 예약 시간을 저장한다.")
    @Test
    void create() {
        //given
        ReservationTimeCreateRequest reservationTimeCreateRequest = TimeFixture.createTimeCreateRequest();

        //when
        ReservationTimeResponse result = reservationTimeService.create(reservationTimeCreateRequest);

        //then
        assertAll(
                () -> assertThat(result.id()).isNotZero(),
                () -> assertThat(result.startAt()).isEqualTo(reservationTimeCreateRequest.startAt())
        );
    }

    @DisplayName("모든 예약 시간 내역을 조회한다.")
    @Test
    void findAll() {
        //given
        reservationTimeRepository.save(TimeFixture.createTime());

        //when
        List<ReservationTimeResponse> reservationTimes = reservationTimeService.findAll();

        //then
        assertThat(reservationTimes).hasSize(1);
    }

    @DisplayName("시간이 이미 존재하면 예외를 발생시킨다.")
    @Test
    void duplicatedTime() {
        //given
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        ReservationTimeCreateRequest reservationTimeCreateRequest = TimeFixture.createTimeCreateRequest(time.getStartAt());

        //when&then
        assertThatThrownBy(() -> reservationTimeService.create(reservationTimeCreateRequest))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("이미 같은 시간이 존재합니다.");
    }

    @DisplayName("예약이 존재하는 시간으로 삭제를 시도하면 예외를 발생시킨다.")
    @Test
    void cannotDeleteTime() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        Schedule schedule = ScheduleFixture.createFutureSchedule(reservationTimeRepository.save(TimeFixture.createTime()));
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));

        //when&then
        long timeId = schedule.getReservationTime().getId();
        assertThatThrownBy(() -> reservationTimeService.deleteById(timeId))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("해당 시간에 예약(대기)이 존재해서 삭제할 수 없습니다.");
    }

    @DisplayName("해당 테마와 날짜에 예약이 가능한 시간 목록을 조회한다.")
    @Test
    void findAvailableTimes() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        ReservationTime bookedReservationTime = reservationTimeRepository.save(TimeFixture.createTime(10, 0));
        ReservationTime notBookedReservationTime = reservationTimeRepository.save(TimeFixture.createTime(12, 0));
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        Schedule schedule = ScheduleFixture.createFutureSchedule(bookedReservationTime);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));

        //when
        ReservationTimeReadRequest request = new ReservationTimeReadRequest(schedule.getDate(), theme.getId());
        List<AvailableReservationTimeResponse> result = reservationTimeService.findAvailableTimes(request);

        //then
        boolean isBookedOfBookedTime = result.stream()
                .filter(bookedTime -> bookedTime.id() == bookedReservationTime.getId())
                .findFirst().get().alreadyBooked();
        boolean isBookedOfUnBookedTime = result.stream()
                .filter(unbookedTime -> unbookedTime.id() == notBookedReservationTime.getId())
                .findFirst().get().alreadyBooked();
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(isBookedOfUnBookedTime).isFalse(),
                () -> assertThat(isBookedOfBookedTime).isTrue()
        );
    }
}
