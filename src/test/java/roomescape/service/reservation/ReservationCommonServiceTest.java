package roomescape.service.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.dto.ReservationWithRank;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;
import roomescape.exception.InvalidReservationException;
import roomescape.fixture.*;
import roomescape.service.ServiceTest;
import roomescape.service.reservation.dto.ReservationFilterRequest;
import roomescape.service.reservation.dto.ReservationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ReservationCommonServiceTest extends ServiceTest {

    @Autowired
    private ReservationCommonService reservationCommonService;
    @Autowired
    private ReservationRepository reservationRepository;

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

    @DisplayName("id로 예약을 삭제한다.")
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

    @DisplayName("예약을 삭제하고, 예약 대기가 있다면 가장 우선순위가 높은 예약 대기를 예약으로 전환한다.")
    @Test
    void deleteThenUpdateReservation() {
        //given
        Member member = memberRepository.save(MemberFixture.createGuest());
        Member anotherMember = memberRepository.save(MemberFixture.createGuest());
        Theme theme = themeRepository.save(ThemeFixture.createTheme());
        ReservationTime time = reservationTimeRepository.save(TimeFixture.createTime());
        Schedule schedule = ScheduleFixture.createFutureSchedule(time);
        ReservationDetail reservationDetail = reservationDetailRepository.save(ReservationDetailFixture.create(theme, schedule));
        Reservation reservation = reservationRepository.save(ReservationFixture.createReserved(member, reservationDetail));
        reservationRepository.save(WaitingFixture.create(anotherMember, reservationDetail));

        //when
        reservationCommonService.deleteById(reservation.getId());

        //then
        List<Boolean> reservations = reservationRepository.findWithRankingByMemberId(anotherMember.getId()).stream()
                .map(ReservationWithRank::getReservation)
                .map(Reservation::isReserved)
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
}
