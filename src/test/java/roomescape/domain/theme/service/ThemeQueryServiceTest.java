package roomescape.domain.theme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.test.fixture.DateFixture.NEXT_DAY;
import static roomescape.test.fixture.DateFixture.TODAY;
import static roomescape.test.fixture.DateFixture.YESTERDAY;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import roomescape.domain.member.domain.Member;
import roomescape.domain.member.domain.Role;
import roomescape.domain.reservation.domain.Reservation;
import roomescape.domain.theme.domain.Theme;
import roomescape.domain.theme.repository.ThemeRepository;
import roomescape.domain.theme.response.FindAllThemeResponse;
import roomescape.domain.time.domain.ReservationTime;

@DataJpaTest
@Import(value = {ThemeQueryService.class})
class ThemeQueryServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ThemeQueryService themeQueryService;

    private Member member;
    private ReservationTime time;

    @BeforeEach
    void setup() {
        time = entityManager.persist(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("모든 테마를 조회할 수 있다.")
    @Test
    void testMethodNameHere() {
        // given
        entityManager.persist(
                Theme.createWithoutId("테마1", "테마 설명", "thumbnail.jpg"));
        entityManager.persist(
                Theme.createWithoutId("테마2", "테마 설명", "thumbnail.jpg"));
        entityManager.persist(
                Theme.createWithoutId("테마2", "테마 설명", "thumbnail.jpg"));

        entityManager.flush();
        entityManager.clear();

        // when
        List<FindAllThemeResponse> allThemes = themeQueryService.findAllThemes();

        // then
        assertThat(allThemes).hasSize(3);
    }

    @Nested
    @DisplayName("인기 테마를 조회할 수 있다.")
    public class findTopThemes {

        @DisplayName("예약 횟수 순으로 테마를 조회할 수 있다.")
        @Test
        void canFindThemeOrderByReservationCount() {
            // given
            Theme firstTheme = entityManager.persist(
                    Theme.createWithoutId("테마1", "테마 설명", "thumbnail.jpg"));
            Theme secondTheme = entityManager.persist(
                    Theme.createWithoutId("테마2", "테마 설명", "thumbnail.jpg"));
            Theme thirdTheme = entityManager.persist(
                    Theme.createWithoutId("테마2", "테마 설명", "thumbnail.jpg"));

            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    YESTERDAY, time, firstTheme, member));
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    TODAY, time, firstTheme, member));
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    NEXT_DAY, time, firstTheme, member));

            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    YESTERDAY, time, secondTheme, member));
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    TODAY, time, secondTheme, member));

            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    YESTERDAY, time, thirdTheme, member));

            entityManager.flush();
            entityManager.clear();

            // when
            List<Theme> themes = themeRepository.findThemesOrderByReservationCount(YESTERDAY, NEXT_DAY, 3);

            // then
            assertThat(themes).containsExactly(firstTheme, secondTheme, thirdTheme);
        }

        @DisplayName("기간을 벗어난 예약은 조회에 포함되지 않는다.")
        @Test
        void cannotIncludeOutOfDate() {
            // given
            Theme firstTheme = entityManager.persist(
                    Theme.createWithoutId("테마1", "테마 설명", "thumbnail.jpg"));
            Theme secondTheme = entityManager.persist(
                    Theme.createWithoutId("테마2", "테마 설명", "thumbnail.jpg"));

            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    TODAY, time, firstTheme, member));
            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    NEXT_DAY, time, secondTheme, member));

            entityManager.flush();
            entityManager.clear();

            // when
            List<Theme> themes = themeRepository.findThemesOrderByReservationCount(YESTERDAY, TODAY, 2);

            // then
            assertThat(themes).containsExactly(firstTheme, secondTheme);
        }

        @DisplayName("기간 동안의 예약이 존재하는 테마가 조회하고자 하는 테마 개수보다 적다면 아무 테마나 조회 개수에 맞춰 출력된다.")
        @Test
        void canFindDefaultTheme() {
            // given
            entityManager.persist(
                    Theme.createWithoutId("테마1", "테마 설명", "thumbnail.jpg"));
            entityManager.persist(
                    Theme.createWithoutId("테마2", "테마 설명", "thumbnail.jpg"));
            entityManager.persist(
                    Theme.createWithoutId("테마2", "테마 설명", "thumbnail.jpg"));

            entityManager.flush();
            entityManager.clear();

            // when
            List<Theme> themes = themeRepository.findThemesOrderByReservationCount(YESTERDAY, NEXT_DAY, 3);

            // then
            assertThat(themes).hasSize(3);
        }
    }
}
