package roomescape.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.Fixture.HORROR_DESCRIPTION;
import static roomescape.Fixture.HORROR_THEME;
import static roomescape.Fixture.HORROR_THEME_NAME;
import static roomescape.Fixture.HOUR_10;
import static roomescape.Fixture.KAKI_EMAIL;
import static roomescape.Fixture.KAKI_NAME;
import static roomescape.Fixture.KAKI_PASSWORD;
import static roomescape.Fixture.MEMBER_JOJO;
import static roomescape.Fixture.RESERVATION_TIME_10_00;
import static roomescape.Fixture.THUMBNAIL;
import static roomescape.Fixture.TOMORROW;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Description;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.ThemeName;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("전체 예약 목록을 조회한다.")
    @Test
    void findAllTest() {
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.parse(HOUR_10)));

        Theme theme = themeRepository.save(
                new Theme(
                        new ThemeName(HORROR_THEME_NAME),
                        new Description(HORROR_DESCRIPTION),
                        THUMBNAIL
                )
        );

        Member member = memberRepository.save(new Member(new MemberName(KAKI_NAME), KAKI_EMAIL, KAKI_PASSWORD));

        reservationRepository.save(new Reservation(member, LocalDate.now(), theme, reservationTime));

        List<Reservation> reservations = reservationRepository.findAll();

        assertThat(reservations).hasSize(1);
    }
// TODO: 빼기
//    @DisplayName("회원 id로 예약 대기 순번 목록을 조회한다.")
//    @Test
//    void findReservationWithRanksByMemberId() {
//        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
//        Theme theme = themeRepository.save(HORROR_THEME);
//        Member jojo = memberRepository.save(MEMBER_JOJO);
//        Member kaki = memberRepository.save(MEMBER_KAKI);
//
//        reservationRepository.save(new Reservation(jojo, TODAY, theme, reservationTime));
//        waitingRepository.save(new Waiting(kaki, TODAY, theme, reservationTime));
//        waitingRepository.save(new Waiting(jojo, TODAY, theme, reservationTime));
//        waitingRepository.save(new Waiting(kaki, TODAY, theme, reservationTime));
//
//        List<ReservationWithRankAndPayment> reservationWithRankAndPayments = reservationRepository.findReservationWithRanksByMemberId(
//                jojo.getId());
//
//        assertAll(
//                () -> assertThat(reservationWithRankAndPayments).hasSize(2),
//                () -> assertThat(reservationWithRankAndPayments.get(1)
//                        .getRank())
//                        .isEqualTo(2L)
//        );
//    }

    @DisplayName("id 값을 받아 Reservation 반환")
    @Test
    void findByIdTest() {
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.parse(HOUR_10)));

        Theme theme = themeRepository.save(
                new Theme(
                        new ThemeName(HORROR_THEME_NAME),
                        new Description(HORROR_DESCRIPTION),
                        THUMBNAIL
                )
        );

        Member member = memberRepository.save(new Member(new MemberName(KAKI_NAME), KAKI_EMAIL, KAKI_PASSWORD));

        Reservation savedReservation = reservationRepository.save(
                new Reservation(member, LocalDate.now(), theme, reservationTime));
        Reservation findReservation = reservationRepository.findById(savedReservation.getId()).get();

        assertThat(findReservation.getMember().getEmail()).isEqualTo(savedReservation.getMember().getEmail());
    }

    @DisplayName("날짜와 테마 아이디로 예약 시간 아이디들을 조회한다.")
    @Test
    void findTimeIdsByDateAndThemeIdTest() {
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.parse(HOUR_10)));

        Theme theme = themeRepository.save(
                new Theme(
                        new ThemeName(HORROR_THEME_NAME),
                        new Description(HORROR_DESCRIPTION),
                        THUMBNAIL
                )
        );

        Member member = memberRepository.save(new Member(new MemberName(KAKI_NAME), KAKI_EMAIL, KAKI_PASSWORD));

        Reservation savedReservation = reservationRepository.save(
                new Reservation(member, LocalDate.now(), theme, reservationTime));

        List<Long> timeIds = reservationRepository.findTimeIdsByDateAndThemeId(savedReservation.getDate(),
                theme.getId());

        assertThat(timeIds).containsExactly(reservationTime.getId());
    }
//
//    @DisplayName("날짜, 시간, 테마가 일치하는 예약 목록을 조회한다.")
//    @Test
//    void findAllByDateAndReservationTimeAndTheme() {
//        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
//        Theme theme = themeRepository.save(HORROR_THEME);
//        Member jojo = memberRepository.save(MEMBER_JOJO);
//        Member kaki = memberRepository.save(MEMBER_KAKI);
//
//        reservationRepository.save(new Reservation(jojo, TOMORROW, theme, reservationTime));
//        reservationRepository.save(new Reservation(kaki, TOMORROW, theme, reservationTime));
//        reservationRepository.save(new Reservation(jojo, TODAY, theme, reservationTime));
//
//        List<Reservation> reservations = reservationRepository.findAllByDateAndReservationTimeAndTheme(
//                TOMORROW,
//                reservationTime,
//                theme
//        );
//
//        assertThat(reservations).hasSize(2);
//    }

    @DisplayName("날짜, 시간, 테마가 일치하는 Reservation을 반환한다.")
    @Test
    void existReservationTest() {
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Member member = memberRepository.save(MEMBER_JOJO);

        Reservation expected = reservationRepository.save(
                new Reservation(member, TOMORROW, theme, reservationTime));

        Optional<Reservation> actual = reservationRepository.findFirstByDateAndReservationTimeAndTheme(
                TOMORROW,
                reservationTime,
                theme
        );

        assertThat(actual).isNotEmpty()
                .get()
                .extracting(Reservation::getId)
                .isEqualTo(expected.getId());
    }

    @DisplayName("날짜, 시간, 테마, 회원 정보가 일치하는 Reservation을 반환한다.")
    @Test
    void findFirstByDateAndReservationTimeAndThemeAndMember() {
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Member member = memberRepository.save(MEMBER_JOJO);

        reservationRepository.save(new Reservation(member, TOMORROW, theme, reservationTime));

        Optional<Reservation> savedReservation = reservationRepository.findFirstByDateAndReservationTimeAndThemeAndMember(
                TOMORROW,
                reservationTime,
                theme,
                member
        );

        assertThat(savedReservation).isNotEmpty();
    }

    @DisplayName("회원 아이디, 테마 아이디와 기간이 일치하는 Reservation을 반환한다.")
    @Test
    void findAllByThemeIdAndMemberIdAndBetweenStartDateAndEndDate() {
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.parse(HOUR_10)));

        Theme theme = themeRepository.save(
                new Theme(
                        new ThemeName(HORROR_THEME_NAME),
                        new Description(HORROR_DESCRIPTION),
                        THUMBNAIL
                )
        );

        Member member = memberRepository.save(new Member(new MemberName(KAKI_NAME), KAKI_EMAIL, KAKI_PASSWORD));

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate oneWeekLater = LocalDate.now().plusWeeks(1);
        reservationRepository.save(new Reservation(member, tomorrow, theme, reservationTime));
        reservationRepository.save(new Reservation(member, oneWeekLater, theme, reservationTime));

        List<Reservation> reservations = reservationRepository.findAllByThemeIdAndMemberIdAndDateBetween(
                theme.getId(),
                member.getId(),
                LocalDate.now(),
                tomorrow
        );

        assertThat(reservations).hasSize(1);
    }

    @DisplayName("예약 삭제 테스트")
    @Test
    void deleteTest() {
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.parse(HOUR_10)));

        Theme theme = themeRepository.save(
                new Theme(
                        new ThemeName(HORROR_THEME_NAME),
                        new Description(HORROR_DESCRIPTION),
                        THUMBNAIL
                )
        );

        Member member = memberRepository.save(new Member(new MemberName(KAKI_NAME), KAKI_EMAIL, KAKI_PASSWORD));

        Reservation savedReservation = reservationRepository.save(
                new Reservation(member, LocalDate.now(), theme, reservationTime));
        reservationRepository.deleteById(savedReservation.getId());

        List<Reservation> reservations = reservationRepository.findAll();

        assertThat(reservations).isEmpty();
    }

    @DisplayName("날짜, 시간, 테마, 상태가 같은 Reservation을 반환한다.")
    @Test
    void findFirstByDateAndReservationTimeAndThemeAndStatus() {
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Member member = memberRepository.save(MEMBER_JOJO);

        Reservation expected = reservationRepository.save(
                new Reservation(member, TOMORROW, theme, reservationTime));

        Optional<Reservation> actual = reservationRepository.findFirstByDateAndReservationTimeAndTheme(
                TOMORROW,
                reservationTime,
                theme
        );
        assertThat(actual).isNotEmpty()
                .get()
                .extracting(Reservation::getId)
                .isEqualTo(expected.getId());
    }
}
