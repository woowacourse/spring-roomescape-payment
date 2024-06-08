package roomescape.web.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.infrastructure.repository.MemberRepository;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.infrastructure.repository.ReservationTimeRepository;
import roomescape.infrastructure.repository.ThemeRepository;
import roomescape.support.DatabaseCleanupListener;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.ThemeFixture;
import roomescape.support.fixture.TimeFixture;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberThemeControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    Reservation reservation(Member member, Theme theme, String date, ReservationTime time, Status status) {
        return new Reservation(member, theme, LocalDate.parse(date), time, status);
    }

    @DisplayName("테마 목록을 조회하는데 성공하면 응답과 200 상태 코드를 반환한다.")
    @Test
    void return_200_when_find_all_themes() {
        themeRepository.save(ThemeFixture.THEME_DATABASE.create());
        themeRepository.save(ThemeFixture.THEME_DREAM.create());
        themeRepository.save(ThemeFixture.THEME_BED.create());
        themeRepository.save(ThemeFixture.THEME_JAVA.create());

        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(4));
    }

    @DisplayName("인기 테마를 조회하는데 성공하면 응답과 200 상태 코드를 반환한다.")
    @Test
    void return_200_when_find_top_booked_themes() {
        Theme database = themeRepository.save(ThemeFixture.THEME_DATABASE.create());
        Theme dream = themeRepository.save(ThemeFixture.THEME_DREAM.create());
        Theme java = themeRepository.save(ThemeFixture.THEME_JAVA.create());
        ReservationTime onePm = timeRepository.save(TimeFixture.ONE_PM.create());
        ReservationTime twoPm = timeRepository.save(TimeFixture.TWO_PM.create());
        ReservationTime threePm = timeRepository.save(TimeFixture.THREE_PM.create());
        Member sun = memberRepository.save(MemberFixture.MEMBER_SUN.create());
        Member jazz = memberRepository.save(MemberFixture.MEMBER_JAZZ.create());
        Member bri = memberRepository.save(MemberFixture.MEMBER_BRI.create());

        LocalDate yesterday = LocalDate.now().minusDays(1);

        reservationRepository.save(reservation(sun, java, yesterday.toString(), onePm, Status.RESERVED));
        reservationRepository.save(reservation(sun, database, yesterday.toString(), threePm, Status.RESERVED));
        reservationRepository.save(reservation(bri, java, yesterday.toString(), twoPm, Status.RESERVED));
        reservationRepository.save(reservation(bri, database, yesterday.toString(), onePm, Status.RESERVED));
        reservationRepository.save(reservation(jazz, database, yesterday.toString(), onePm, Status.WAITING));
        reservationRepository.save(reservation(jazz, dream, yesterday.toString(), onePm, Status.WAITING));

        List<ThemeResponse> actualResponse = RestAssured.given().log().all()
                .when().get("/themes/ranking")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", ThemeResponse.class);

        List<ThemeResponse> expectedResponse = List.of(
                new ThemeResponse(database.getId(), database.getName(), database.getDescription(),
                        database.getThumbnail()),
                new ThemeResponse(java.getId(), java.getName(), java.getDescription(), java.getThumbnail()),
                new ThemeResponse(dream.getId(), dream.getName(), dream.getDescription(), dream.getThumbnail())
        );

        assertThat(actualResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }
}
