package roomescape.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.application.dto.request.time.ReservationTimeRequest;
import roomescape.application.dto.response.time.AvailableReservationTimeResponse;
import roomescape.application.dto.response.time.ReservationTimeResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.fixture.CommonFixture;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationDetailFixture;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;

class ReservationTimeServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationDetailRepository reservationDetailRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("예약 시간을 저장한다")
    @Test
    void when_saveReservationTime_then_saveSuccess() {
        // given
        ReservationTimeRequest request = new ReservationTimeRequest(CommonFixture.now);

        // when
        ReservationTimeResponse reservationTime = reservationTimeService.saveReservationTime(request);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservationTime.id()).isNotNull();
            softly.assertThat(reservationTime.startAt()).isEqualTo(CommonFixture.now);
        });
    }

    @DisplayName("모든 예약 시간을 조회한다")
    @Test
    void when_findAllReservationTimes_then_returnAllReservationTimes() {
        // given
        List<ReservationTime> times = TimeFixture.createTimes(5);
        times.forEach(reservationTimeRepository::save);

        // when
        List<ReservationTimeResponse> reservationTimes = reservationTimeService.findAllReservationTime();

        // then
        Assertions.assertThat(reservationTimes).hasSize(5);
    }

    @DisplayName("예약 가능한 시간을 조회한다")
    @Test
    void when_findAllAvailableReservationTime_then_returnAvailableReservationTimes() {
        // given
        Member member = MemberFixture.createMember("name");
        memberRepository.save(member);
        Theme theme = ThemeFixture.createTheme("테마1");
        themeRepository.save(theme);
        List<ReservationTime> times = List.of(
                TimeFixture.createTimeAtExact(8),
                TimeFixture.createTimeAtExact(9),
                TimeFixture.createTimeAtExact(10),
                TimeFixture.createTimeAtExact(11),
                TimeFixture.createTimeAtExact(12),
                TimeFixture.createTimeAtExact(13)
        );
        times.forEach(reservationTimeRepository::save);
        times.subList(0, 5).forEach(time -> {
            ReservationDetail detail = ReservationDetailFixture.createReservationDetail(CommonFixture.today, time,
                    theme);
            reservationDetailRepository.save(detail);
            Reservation reservation = ReservationFixture.createReservation(detail, member, Status.RESERVED);
            reservationRepository.save(reservation);
        });

        // when
        List<AvailableReservationTimeResponse> availableTimes = reservationTimeService.findAllAvailableReservationTime(
                LocalDate.now(), theme.getId());

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(availableTimes).hasSize(6);
            softly.assertThat(availableTimes.get(0).alreadyBooked()).isTrue();
            softly.assertThat(availableTimes.get(1).alreadyBooked()).isTrue();
            softly.assertThat(availableTimes.get(2).alreadyBooked()).isTrue();
            softly.assertThat(availableTimes.get(3).alreadyBooked()).isTrue();
            softly.assertThat(availableTimes.get(4).alreadyBooked()).isTrue();
            softly.assertThat(availableTimes.get(5).alreadyBooked()).isFalse();
        });
    }

    @DisplayName("예약 시간을 삭제한다")
    @Test
    void when_deleteReservationTime_then_deleteSuccess() {
        // given
        ReservationTime reservationTime = TimeFixture.createTime(LocalTime.now());
        ReservationTime time = reservationTimeRepository.save(reservationTime);
        Long timeId = time.getId();

        // when
        reservationTimeService.deleteReservationTime(time.getId());

        // then
        Assertions.assertThat(reservationTimeRepository.findReservationTime(timeId)).isEmpty();
    }
}
