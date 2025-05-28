package roomescape.integration.service;

import static org.assertj.core.api.Assertions.*;
import static roomescape.common.Constant.FIXED_CLOCK;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.GlobalConfig;
import roomescape.global.config.ClockConfig;
import roomescape.integration.fixture.ReservationScheduleDbFixture;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberEncodedPassword;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDateTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.schedule.domain.ReservationDate;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.controller.TimeService;
import roomescape.time.controller.dto.CreateReservationTimeRequest;
import roomescape.time.controller.dto.ReservationTimeResponse;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

@Transactional
@SpringBootTest
@Import(GlobalConfig.class)
class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TimeService service;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationScheduleDbFixture reservationScheduleDbFixture;

    @Test
    void 예약시간을_생성할_수_있다() {
        // given
        LocalTime startAt = LocalTime.of(10, 0);
        CreateReservationTimeRequest request = new CreateReservationTimeRequest(startAt);

        // when
        ReservationTimeResponse response = service.createReservationTime(request);

        // then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response.startAt()).isEqualTo(startAt);
        softly.assertAll();
    }

    @Test
    void 중복된_예약시간은_생성할_수_없다() {
        // given
        LocalTime startAt = LocalTime.of(10, 0);
        reservationTimeRepository.save(new ReservationTime(null, startAt));
        CreateReservationTimeRequest request = new CreateReservationTimeRequest(startAt);

        // when & then
        assertThatThrownBy(() -> service.createReservationTime(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 모든_예약시간을_조회할_수_있다() {
        // given
        reservationTimeRepository.save(new ReservationTime(null, LocalTime.of(10, 0)));
        reservationTimeRepository.save(new ReservationTime(null, LocalTime.of(11, 0)));

        // when
        List<ReservationTimeResponse> result = service.findAllReservationTimes();

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 예약이_없는_시간은_삭제할_수_있다() {
        // given
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(null, LocalTime.of(10, 0)));

        // when
        service.deleteReservationTimeById(time.getId());

        // then
        assertThat(reservationTimeRepository.findById(time.getId())).isNotPresent();
    }

    @Test
    void 예약이_존재하는_시간은_삭제할_수_없다() {
        // given
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(null, LocalTime.of(10, 0)));
        ReservationDate date = new ReservationDate(LocalDate.of(2025, 5, 5));
        ReservationDateTime reservationDateTime = new ReservationDateTime(date, time, FIXED_CLOCK);
        Member member = memberRepository.save(new Member(
                null,
                new MemberName("한스"),
                new MemberEmail("leehyeonsu4888@gmail.com"),
                new MemberEncodedPassword("dsa"),
                MemberRole.MEMBER
        ));
        Theme theme = themeRepository.save(new Theme(
                null,
                new ThemeName("공포"),
                new ThemeDescription("공포입니다."),
                new ThemeThumbnail("썸네일")
        ));
        ReservationSchedule schedule = reservationScheduleDbFixture.createSchedule(date, time, theme);
        reservationRepository.save(new Reservation(null, member, schedule));

        // when & then
        assertThatThrownBy(() -> service.deleteReservationTimeById(time.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
