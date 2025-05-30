package roomescape.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static roomescape.fixture.IntegrationFixture.ADMIN_EMAIL;
import static roomescape.fixture.IntegrationFixture.FUTURE_DATE;
import static roomescape.fixture.IntegrationFixture.PASSWORD;
import static roomescape.fixture.IntegrationFixture.TOKEN;
import static roomescape.fixture.IntegrationFixture.createRegularReservation;
import static roomescape.fixture.IntegrationFixture.createReservationTime;
import static roomescape.fixture.IntegrationFixture.createTheme;
import static roomescape.fixture.IntegrationFixture.findThemesBySize;
import static roomescape.fixture.IntegrationFixture.loginAndGetAuthToken;
import static roomescape.fixture.IntegrationFixture.makeWaitingReservations;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import roomescape.fixture.TestFixture;
import roomescape.member.presentation.dto.response.MemberWebResponse;
import roomescape.reservation.presentation.dto.response.ConfirmedReservationWebResponse;
import roomescape.reservation.presentation.dto.response.WaitingWebResponse;
import roomescape.reservationslot.presentation.dto.response.ReservationResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
class AdminTest {

    @LocalServerPort
    private int port;

    private String ADMIN_TOKEN;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        ADMIN_TOKEN = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
    }

    @Test
    void 관리자_페이지에_접근할_수_있다() {
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 관리자_예약_페이지에_접근할_수_있다() {
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        RestAssured.given().log().all()
                .cookie(TOKEN, authToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 잘못된_시간_형식으로는_예약시간을_등록할_수_없다() {
        Map<String, String> reservationTime = new HashMap<>();
        reservationTime.put("startAt", "10 00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservationTime)
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    void 예약을_삭제한다() {
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L);

        RestAssured.given().log().all()
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 테마를_생성하고_삭제한다() {
        createTheme("추리");
        findThemesBySize(1);

        RestAssured.given().log().all()
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().delete("/admin/themes/1")
                .then().log().all()
                .statusCode(204);
        findThemesBySize(0);
    }


    @Test
    void 예약시간을_생성하고_삭제한다() {
        createReservationTime();

        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().delete("/admin/times/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void 예약을_추가한다() {
        createReservationTime();
        createTheme("추리");

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", FUTURE_DATE);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("memberId", 2);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    void 대기중인_예약을_조회한다() {
        makeWaitingReservations();
        List<WaitingWebResponse> responses = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().get("/admin/waiting-reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        assertThat(responses.size()).isOne();
    }

    @Test
    void 대기중인_예약을_삭제한다() {
        ReservationResponse waitingResponse = makeWaitingReservations();
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, ADMIN_TOKEN)
                .pathParam("waitingId", waitingResponse.waitingId())
                .when().delete("/admin/waiting-reservations/{waitingId}")
                .then().log().all()
                .statusCode(204);

        List<WaitingWebResponse> responses = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().get("/admin/waiting-reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        assertThat(responses).isEmpty();
    }

    @Test
    void 모든_예약을_조회한다() {
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L);

        RestAssured.given().log().all()
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void 예약을_필터링할_수_있다() {
        createReservationTime();
        createTheme("추리");
        createTheme("로맨스");
        createRegularReservation(1L);
        createRegularReservation(2L);

        List<ConfirmedReservationWebResponse> reservationsFilteredByThemeId = RestAssured.given().log().all()
                .cookie(TOKEN, ADMIN_TOKEN)
                .when().queryParams("themeId", 1L, "memberId", 2L, "dateFrom", FUTURE_DATE,
                        "dateTo", TestFixture.makeAfterOneWeekDate().plusDays(1).toString())
                .get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        assertThat(reservationsFilteredByThemeId.size()).isEqualTo(1);
    }


    @Test
    void 모든_일반_멤버들을_조회한다() {
        List<MemberWebResponse> memberWebRespons = RestAssured.given().log().all()
                .cookie(TOKEN, ADMIN_TOKEN)
                .contentType(ContentType.JSON)
                .when().get("/admin/members")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        assertThat(memberWebRespons.size()).isEqualTo(2);
    }
}
