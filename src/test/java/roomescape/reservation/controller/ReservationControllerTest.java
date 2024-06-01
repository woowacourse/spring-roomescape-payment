package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static roomescape.fixture.MemberFixture.MEMBER_ADMIN;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.fixture.CookieProvider;
import roomescape.fixture.RestAssuredTemplate;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.payment.PaymentType;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.time.domain.ReservationTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
class ReservationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockBean
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("예약을 조회, 추가, 삭제할 수 있다.")
    @Test
    void findCreateDeleteReservations() {
        doNothing().when(paymentService).approvePayment(any());

        Cookies cookies = RestAssuredTemplate.makeUserCookie(MEMBER_BRI);
        LocalDate date = LocalDate.now().plusDays(1);
        Long themeId = RestAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), cookies).id();
        Long timeId = RestAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), cookies).id();

        ReservationCreateRequest params = new ReservationCreateRequest(date, timeId, themeId, "paymentKey", "orderId",
                1000, PaymentType.NORMAL);

        // 예약 추가
        ReservationResponse response = RestAssured.given().log().all()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getObject("", ReservationResponse.class);

        // 예약 조회
        List<ReservationResponse> reservationResponses = RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("", ReservationResponse.class);

        assertThat(reservationResponses).containsExactlyInAnyOrder(response);

        // 예약 삭제
        RestAssured.given().log().all()
                .when().delete("/reservations/" + response.id())
                .then().log().all()
                .statusCode(204);

        // 예약 조회
        reservationResponses = RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("", ReservationResponse.class);

        assertThat(reservationResponses).isEmpty();
    }

    @DisplayName("예약 추가 시 인자 중 null이 있을 경우, 예약을 추가할 수 없다.")
    @Test
    void createReservation_whenNameIsNull() {
        Cookies cookies = RestAssuredTemplate.makeUserCookie(MEMBER_ADMIN);
        Long themeId = RestAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), cookies).id();
        Long timeId = RestAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), cookies).id();

        AdminReservationCreateRequest params = new AdminReservationCreateRequest
                (null, null, timeId, themeId);
        Cookies userCookies = CookieProvider.makeUserCookie();

        RestAssured.given().log().all()
                .cookies(userCookies)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("detail", is("인자 중 null 값이 존재합니다."));
    }

    @DisplayName("예약 삭제 시 예약 대기가 존재하지 않는다면 삭제된다.")
    @Test
    void deleteReservation_whenWaitingNotExists() {
        Cookies cookies = RestAssuredTemplate.makeUserCookie(MEMBER_ADMIN);
        LocalDate date = LocalDate.now().plusDays(1);
        Long themeId = RestAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), cookies).id();
        Long timeId = RestAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), cookies).id();

        AdminReservationCreateRequest reservationParams =
                new AdminReservationCreateRequest(MEMBER_ADMIN.getId(), date, timeId, themeId);
        ReservationResponse response = RestAssuredTemplate.create(reservationParams, cookies);

        // 예약 삭제
        RestAssured.given().log().all()
                .when().delete("/reservations/" + response.id())
                .then().log().all()
                .statusCode(204);

        // 예약 조회
        List<ReservationResponse> reservationResponses = RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList("", ReservationResponse.class);

        assertThat(reservationResponses).doesNotContain(response);
    }

    @DisplayName("예약 삭제 시 과거 예약이라면 예외가 발생한다.")
    @Test
    @Sql(scripts = {"/init.sql", "/past-reservation-data.sql"})
    void deleteReservation_whenPastReservation() {
        Cookies cookies = RestAssuredTemplate.makeUserCookie(MEMBER_ADMIN);

        // 예약 삭제
        RestAssured.given().log().all()
                .when().delete("/reservations/" + 1L)
                .then().log().all()
                .statusCode(400)
                .body("detail", is("과거 예약에 대한 취소는 불가능합니다."));
        ;
    }

    @DisplayName("예약 삭제 시 과거 예약이라면 예외가 발생한다.")
    @Test
    void deleteReservation_whenEqualsDateReservation() {
        Cookies cookies = RestAssuredTemplate.makeUserCookie(MEMBER_ADMIN);
        LocalDate todayDate = LocalDate.now();
        ReservationTime time = new ReservationTime(LocalTime.now().plusMinutes(10));

        Long themeId = RestAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), cookies).id();
        Long timeId = RestAssuredTemplate.create(TimeFixture.toTimeCreateRequest(time), cookies).id();

        AdminReservationCreateRequest reservationParams =
                new AdminReservationCreateRequest(MEMBER_ADMIN.getId(), todayDate, timeId, themeId);
        ReservationResponse response = RestAssuredTemplate.create(reservationParams, cookies);

        // 예약 삭제
        RestAssured.given().log().all()
                .when().delete("/reservations/" + response.id())
                .then().log().all()
                .statusCode(400)
                .body("detail", is("당일 예약 취소는 불가능합니다."));
    }
}
