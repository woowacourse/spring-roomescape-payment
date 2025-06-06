package roomescape.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.test.fixture.DateFixture.TODAY;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import roomescape.exception.BadRequestException;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.domain.Role;
import roomescape.mvc.reservation.domain.Reservation;
import roomescape.mvc.reservation.service.ReservationQueryService;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.theme.dto.ThemeCreationContent;
import roomescape.mvc.theme.response.AddThemeResponse;
import roomescape.mvc.theme.service.ThemeQueryService;
import roomescape.mvc.theme.service.ThemeService;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.mvc.waiting.domain.Waiting;
import roomescape.mvc.waiting.service.WaitingQueryService;

@DataJpaTest
@Import(value = {ThemeQueryService.class, ReservationQueryService.class, WaitingQueryService.class, ThemeService.class})
class ThemeServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ThemeService themeService;

    private ReservationTime time;
    private Member member;

    @BeforeEach
    void setup() {
        time = entityManager.persist(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("테마를 추가할 수 있다.")
    @Test
    void canAddTheme() {
        // given
        ThemeCreationContent creationContent = new ThemeCreationContent("테마", "설명", "섬네일");

        // when
        AddThemeResponse response = themeService.addTheme(creationContent);

        // then
        Theme expectedTheme = entityManager.find(Theme.class, response.id());
        assertAll(
                () -> assertThat(response.id()).isEqualTo(expectedTheme.getId()),
                () -> assertThat(response.name()).isEqualTo(creationContent.name()),
                () -> assertThat(response.thumbnail()).isEqualTo(creationContent.thumbnail()),
                () -> assertThat(response.description()).isEqualTo(creationContent.description())
        );
    }

    @Nested
    @DisplayName("테마를 삭제할 수 있다.")
    public class deleteThemeById {

        @DisplayName("테마를 성공적으로 삭제할 수 있다.")
        @Test
        void canDeleteTheme() {
            // given
            Theme theme = entityManager.persist(
                    Theme.createWithoutId("테마1", "테마 설명", "thumbnail.jpg"));

            entityManager.flush();
            entityManager.clear();

            // when
            themeService.deleteThemeById(theme.getId());

            // then
            assertThat(entityManager.find(Theme.class, theme.getId())).isNull();
        }

        @DisplayName("이미 예약이 존재하는 경우 테마를 삭제할 수 없다.")
        @Test
        void cannotDeleteThemeByReservation() {
            // given
            Theme theme = entityManager.persist(
                    Theme.createWithoutId("테마1", "테마 설명", "thumbnail.jpg"));

            entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    TODAY, time, theme, member));

            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThatThrownBy(() -> themeService.deleteThemeById(theme.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 예약이 존재하는 테마입니다.");
        }

        @DisplayName("이미 예약 대기가 존재하는 경우 테마를 삭제할 수 없다.")
        @Test
        void cannotDeleteThemeByWaiting() {
            // given
            Theme theme = entityManager.persist(
                    Theme.createWithoutId("테마1", "테마 설명", "thumbnail.jpg"));

            entityManager.persist(Waiting.createWithoutIdWithoutPayment(
                    TODAY, theme, time, member));

            entityManager.flush();
            entityManager.clear();

            // when & then
            assertThatThrownBy(() -> themeService.deleteThemeById(theme.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 예약 대기가 존재하는 테마입니다.");
        }
    }
}
