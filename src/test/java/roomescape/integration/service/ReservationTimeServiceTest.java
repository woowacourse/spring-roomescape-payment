package roomescape.integration.service;//package roomescape.integration.service;
//
//import static org.assertj.core.api.Assertions.*;
//import static roomescape.common.Constant.FIXED_CLOCK;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.NoSuchElementException;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.transaction.annotation.Transactional;
//import roomescape.common.ClockConfig;
//import roomescape.integration.fixture.ReservationScheduleDbFixture;
//import roomescape.service.TimeQueryService;
//import roomescape.temp.member.domain.Member;
//import roomescape.temp.member.domain.MemberEmail;
//import roomescape.temp.member.domain.MemberEncodedPassword;
//import roomescape.temp.member.domain.MemberName;
//import roomescape.temp.member.domain.MemberRole;
//import roomescape.temp.member.repository.MemberRepository;
//import roomescape.temp.reservation.domain.Reservation;
//import roomescape.temp.reservation.domain.ReservationDateTime;
//import roomescape.temp.reservation.repository.ReservationRepository;
//import roomescape.temp.schedule.domain.ReservationDate;
//import roomescape.temp.schedule.domain.ReservationSchedule;
//import roomescape.temp.theme.domain.Theme;
//import roomescape.temp.theme.domain.ThemeDescription;
//import roomescape.temp.theme.domain.ThemeName;
//import roomescape.temp.theme.domain.ThemeThumbnail;
//import roomescape.temp.theme.repository.ThemeRepository;
//import roomescape.temp.time.controller.dto.CreateReservationTimeRequest;
//import roomescape.temp.time.controller.dto.ReservationTimeResponse;
//import roomescape.temp.time.domain.ReservationTime;
//import roomescape.temp.time.repository.ReservationTimeRepository;
//
//@Transactional
//@SpringBootTest
//@Import(ClockConfig.class)
//class ReservationTimeServiceTest {
//
//    @Autowired
//    private ReservationTimeRepository reservationTimeRepository;
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    @Autowired
//    private TimeQueryService service;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private ThemeRepository themeRepository;
//    @Autowired
//    private ReservationScheduleDbFixture reservationScheduleDbFixture;
//
//    @Test
//    void 예약시간을_생성할_수_있다() {
//        // given
//        LocalTime startAt = LocalTime.of(10, 0);
//        CreateReservationTimeRequest request = new CreateReservationTimeRequest(startAt);
//
//        // when
//        ReservationTimeResponse response = service.createReservationTime(request);
//
//        // then
//        SoftAssertions softly = new SoftAssertions();
//        softly.assertThat(response.startAt()).isEqualTo(startAt);
//        softly.assertAll();
//    }
//
//    @Test
//    void 중복된_예약시간은_생성할_수_없다() {
//        // given
//        LocalTime startAt = LocalTime.of(10, 0);
//        reservationTimeRepository.save(new ReservationTime(null, startAt));
//        CreateReservationTimeRequest request = new CreateReservationTimeRequest(startAt);
//
//        // when & then
//        assertThatThrownBy(() -> service.createReservationTime(request))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    void 모든_예약시간을_조회할_수_있다() {
//        // given
//        reservationTimeRepository.save(new ReservationTime(null, LocalTime.of(10, 0)));
//        reservationTimeRepository.save(new ReservationTime(null, LocalTime.of(11, 0)));
//
//        // when
//        List<ReservationTimeResponse> result = service.findAllReservationTimes();
//
//        // then
//        assertThat(result).hasSize(2);
//    }
//
//    @Test
//    void 예약이_없는_시간은_삭제할_수_있다() {
//        // given
//        ReservationTime time = reservationTimeRepository.save(new ReservationTime(null, LocalTime.of(10, 0)));
//
//        // when
//        service.deleteReservationTimeById(time.getId());
//
//        // then
//        assertThat(reservationTimeRepository.findById(time.getId())).isNotPresent();
//    }
//
//    @Test
//    void 예약이_존재하는_시간은_삭제할_수_없다() {
//        // given
//        ReservationTime time = reservationTimeRepository.save(new ReservationTime(null, LocalTime.of(10, 0)));
//        ReservationDate date = new ReservationDate(LocalDate.of(2025, 5, 5));
//        ReservationDateTime reservationDateTime = new ReservationDateTime(date, time, FIXED_CLOCK);
//        Member member = memberRepository.save(new Member(
//                null,
//                new MemberName("한스"),
//                new MemberEmail("leehyeonsu4888@gmail.com"),
//                new MemberEncodedPassword("dsa"),
//                MemberRole.MEMBER
//        ));
//        Theme theme = themeRepository.save(new Theme(
//                null,
//                new ThemeName("공포"),
//                new ThemeDescription("공포입니다."),
//                new ThemeThumbnail("썸네일")
//        ));
//        ReservationSchedule schedule = reservationScheduleDbFixture.createSchedule(date, time, theme);
//        reservationRepository.save(new Reservation(null, member, schedule));
//
//        // when & then
//        assertThatThrownBy(() -> service.deleteReservationTimeById(time.getId()))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @Test
//    void 존재하지_않는_예약시간은_삭제할_수_없다() {
//        // when & then
//        assertThatThrownBy(() -> service.deleteReservationTimeById(1L))
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
//    @Test
//    void 예약시간을_ID로_조회할_수_있다() {
//        // given
//        ReservationTime time = reservationTimeRepository.save(new ReservationTime(null, LocalTime.of(10, 0)));
//
//        // when
//        ReservationTime found = service.getReservationTime(time.getId());
//
//        // then
//        assertThat(found.getStartAt()).isEqualTo(LocalTime.of(10, 0));
//    }
//
//    @Test
//    void 예약시간_ID로_조회시_없으면_예외() {
//        // when & then
//        assertThatThrownBy(() -> service.getReservationTime(1L))
//                .isInstanceOf(NoSuchElementException.class);
//    }
//}
