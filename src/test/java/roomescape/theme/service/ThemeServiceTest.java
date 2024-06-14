package roomescape.theme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.ThemeFixture.THEME_2;
import static roomescape.fixture.ThemeFixture.THEME_3;
import static roomescape.fixture.TimeFixture.TIME_1;

import io.restassured.RestAssured;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeCreateRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.TimeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ThemeServiceTest {
    @LocalServerPort
    private int port;
    @Autowired
    private ThemeService themeService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        Theme theme1 = themeRepository.save(THEME_1);
        Theme theme2 = themeRepository.save(THEME_2);
        Theme theme3 = themeRepository.save(THEME_3);
        ReservationTime time = timeRepository.save(TIME_1);
        reservationRepository.save(new Reservation(
                1L, MEMBER_BRI, LocalDate.now().minusDays(1), time, theme2, ReservationStatus.RESERVED));
        reservationRepository.save(new Reservation(
                2L, MEMBER_BRI, LocalDate.now().minusDays(2), time, theme2, ReservationStatus.RESERVED));
        reservationRepository.save(new Reservation(
                3L, MEMBER_BRI, LocalDate.now().minusDays(3), time, theme1, ReservationStatus.RESERVED));
    }

    @DisplayName("모든 테마를 조회할 수 있다.")
    @Test
    void findThemesTest() {
        assertThat(themeService.findThemes())
                .containsExactlyInAnyOrder(
                        new ThemeResponse(1L, THEME_1.getName(), THEME_1.getDescription(), THEME_1.getThumbnail()),
                        new ThemeResponse(2L, THEME_2.getName(), THEME_2.getDescription(), THEME_2.getThumbnail()),
                        new ThemeResponse(3L, THEME_3.getName(), THEME_3.getDescription(), THEME_3.getThumbnail())
                );
    }

    @DisplayName("인기 테마를 조회할 수 있다.")
    @Test
    void findPopularThemesTest() {
        assertThat(themeService.findPopularThemes())
                .isEqualTo(List.of(
                        new ThemeResponse(2L, THEME_2.getName(), THEME_2.getDescription(), THEME_2.getThumbnail()),
                        new ThemeResponse(1L, THEME_1.getName(), THEME_1.getDescription(), THEME_1.getThumbnail())
                ));
    }

    @DisplayName("테마를 생성할 수 있다.")
    @Test
    void createThemeTest() {
        ThemeCreateRequest request = new ThemeCreateRequest("우테코 탈출", "우테코 탈출하기", "https://img.jpg");
        ThemeResponse expected = new ThemeResponse(4L, "우테코 탈출", "우테코 탈출하기", "https://img.jpg");

        assertThat(themeService.createTheme(request)).isEqualTo(expected);
    }

    @DisplayName("테마 생성 시, 이름이 중복된다면 예외를 던진다.")
    @Test
    void createThemeTest_whenExistsName() {
        ThemeCreateRequest request = new ThemeCreateRequest(THEME_1.getName(), "새로운 설명", "https://img.jpg");

        assertThatThrownBy(() -> themeService.createTheme(request))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("테마 이름은 중복될 수 없습니다.");
    }
}
