package roomescape.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.Fixture.HORROR_DESCRIPTION;
import static roomescape.Fixture.HORROR_THEME_NAME;
import static roomescape.Fixture.JOJO_EMAIL;
import static roomescape.Fixture.JOJO_NAME;
import static roomescape.Fixture.JOJO_PASSWORD;
import static roomescape.Fixture.KAKI_EMAIL;
import static roomescape.Fixture.KAKI_NAME;
import static roomescape.Fixture.KAKI_PASSWORD;
import static roomescape.Fixture.THUMBNAIL;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.ThemeName;

@DataJpaTest
class ThemeRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("id로 엔티티를 찾는다.")
    @Test
    void findByIdTest() {
        Theme theme = new Theme(new ThemeName(HORROR_THEME_NAME), new Description(HORROR_DESCRIPTION), THUMBNAIL);
        Theme savedTheme = themeRepository.save(theme);
        Theme findTheme = themeRepository.findById(savedTheme.getId()).get();

        assertAll(
                () -> assertThat(findTheme.getName()).isEqualTo(HORROR_THEME_NAME),
                () -> assertThat(findTheme.getDescription()).isEqualTo(HORROR_DESCRIPTION),
                () -> assertThat(findTheme.getThumbnail()).isEqualTo(THUMBNAIL)
        );
    }

    @DisplayName("이름으로 엔티티를 찾는다.")
    @Test
    void findByIdNameTest() {
        Theme theme = new Theme(new ThemeName(HORROR_THEME_NAME), new Description(HORROR_DESCRIPTION), THUMBNAIL);
        Theme savedTheme = themeRepository.save(theme);
        Theme findTheme = themeRepository.findFirstByThemeName(new ThemeName(savedTheme.getName())).get();

        assertAll(
                () -> assertThat(findTheme.getName()).isEqualTo(HORROR_THEME_NAME),
                () -> assertThat(findTheme.getDescription()).isEqualTo(HORROR_DESCRIPTION),
                () -> assertThat(findTheme.getThumbnail()).isEqualTo(THUMBNAIL)
        );
    }

    @DisplayName("전체 엔티티를 조회한다.")
    @Test
    void findAllTest() {
        Theme theme = new Theme(new ThemeName(HORROR_THEME_NAME), new Description(HORROR_DESCRIPTION), THUMBNAIL);
        themeRepository.save(theme);
        List<Theme> themes = themeRepository.findAll();

        assertThat(themes).hasSize(1);
    }

    @DisplayName("테마 ID로 예약이 참조된 테마들을 찾는다.")
    @Test
    void findReservationInSameIdTest() {
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));

        Theme theme = themeRepository.save(
                new Theme(
                        new ThemeName(HORROR_THEME_NAME),
                        new Description(HORROR_DESCRIPTION),
                        THUMBNAIL
                )
        );

        Member member = memberRepository.save(new Member(new MemberName(KAKI_NAME), KAKI_EMAIL, KAKI_PASSWORD));

        reservationRepository.save(
                new Reservation(member, LocalDate.now(), theme, reservationTime, Status.SUCCESS));
        boolean exist = !themeRepository.findThemesThatReservationReferById(theme.getId()).isEmpty();

        assertThat(exist).isTrue();
    }

    @DisplayName("n일 이후의 날짜를 기준으로 n개의 인기 테마를 조회한다.")
    @Test
    void findTopTenThemesDescendingOfLastWeekTest() {
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));

        Theme theme1 = themeRepository.save(
                new Theme(
                        new ThemeName(HORROR_THEME_NAME),
                        new Description(HORROR_DESCRIPTION),
                        THUMBNAIL
                )
        );

        Theme theme2 = themeRepository.save(
                new Theme(
                        new ThemeName("액션"),
                        new Description("액션 탈출"),
                        THUMBNAIL
                )
        );

        Member kaki = memberRepository.save(new Member(new MemberName(KAKI_NAME), KAKI_EMAIL, KAKI_PASSWORD));
        Member jojo = memberRepository.save(new Member(new MemberName(JOJO_NAME), JOJO_EMAIL, JOJO_PASSWORD));

        reservationRepository.save(
                new Reservation(kaki, LocalDate.now(), theme1, reservationTime, Status.SUCCESS));
        reservationRepository.save(
                new Reservation(kaki, LocalDate.now(), theme2, reservationTime, Status.SUCCESS));
        reservationRepository.save(
                new Reservation(jojo, LocalDate.now(), theme2, reservationTime, Status.SUCCESS));

        LocalDate dateFrom = LocalDate.now().minusWeeks(1);
        List<Theme> themes = themeRepository.findPopularThemesDescOfLastWeekForLimit(dateFrom, 2);

        assertAll(
                () -> assertThat(themes.get(0).getName()).isEqualTo("액션"),
                () -> assertThat(themes).hasSize(2)
        );
    }

    @DisplayName("id를 받아 삭제한다.")
    @Test
    void deleteTest() {
        Theme theme = themeRepository.save(
                new Theme(
                        new ThemeName(HORROR_THEME_NAME),
                        new Description(HORROR_DESCRIPTION),
                        THUMBNAIL
                )
        );
        themeRepository.deleteById(theme.getId());
        List<Theme> themes = themeRepository.findAll();

        assertThat(themes).isEmpty();
    }
}
