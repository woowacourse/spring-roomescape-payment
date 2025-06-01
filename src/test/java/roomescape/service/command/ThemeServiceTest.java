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
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.domain.Waiting;
import roomescape.dto.business.ThemeCreationContent;
import roomescape.dto.response.ThemeResponse;
import roomescape.exception.BadRequestException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;
import roomescape.repository.WaitingRepository;
import roomescape.service.query.ReservationQueryService;
import roomescape.service.query.ThemeQueryService;
import roomescape.service.query.WaitingQueryService;

@DataJpaTest
class ThemeServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private MemberRepository memberRepository;

    private ThemeQueryService themeQueryService;
    private ReservationQueryService reservationQueryService;
    private WaitingQueryService waitingQueryService;
    private ThemeService themeService;

    @BeforeEach
    void setup() {
        themeQueryService = new ThemeQueryService(themeRepository);
        reservationQueryService = new ReservationQueryService(
                reservationRepository, memberRepository, waitingRepository);
        waitingQueryService = new WaitingQueryService(waitingRepository);
        themeService = new ThemeService(
                themeRepository, themeQueryService, reservationQueryService, waitingQueryService);
    }

    @DisplayName("테마를 추가할 수 있다.")
    @Test
    void canAddTheme() {
        // given
        ThemeCreationContent creationContent = new ThemeCreationContent("테마", "설명", "섬네일");

        // when
        ThemeResponse response = themeService.addTheme(creationContent);

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

            // when
            themeService.deleteThemeById(theme.getId());

            // then
            assertThat(entityManager.find(Theme.class, theme.getId())).isNull();
        }

        @DisplayName("이미 예약이 존재하는 경우 테마를 삭제할 수 없다.")
        @Test
        void cannotDeleteThemeByReservation() {
            // given
            ReservationTime reservationTime = entityManager.persist(
                    ReservationTime.createWithoutId(LocalTime.of(10, 0)));

            Member member = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

            Theme theme = entityManager.persist(
                    Theme.createWithoutId("테마1", "테마 설명", "thumbnail.jpg"));

            Reservation reservation = entityManager.persist(Reservation.createWithoutIdAndPaymentHistory(
                    TODAY, reservationTime, theme, member));

            entityManager.flush();

            // when & then
            assertThatThrownBy(() -> themeService.deleteThemeById(theme.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 예약이 존재하는 테마입니다.");
        }

        @DisplayName("이미 예약 대기가 존재하는 경우 테마를 삭제할 수 없다.")
        @Test
        void cannotDeleteThemeByWaiting() {
            // given
            ReservationTime time = entityManager.persist(
                    ReservationTime.createWithoutId(LocalTime.of(10, 0)));

            Member member = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

            Theme theme = entityManager.persist(
                    Theme.createWithoutId("테마1", "테마 설명", "thumbnail.jpg"));

            Waiting waiting = entityManager.persist(Waiting.createWithoutIdWithoutPayment(
                    TODAY, theme, time, member));

            entityManager.flush();

            // when & then
            assertThatThrownBy(() -> themeService.deleteThemeById(theme.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 예약 대기가 존재하는 테마입니다.");
        }
    }
}
