package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.request.AdminReservationRequest;
import roomescape.controller.request.MemberLoginRequest;
import roomescape.controller.request.ReservationRequest;
import roomescape.service.PaymentService;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/initialize_table.sql", "/controller_test_data.sql"})
class ReservationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ReservationController reservationController;

    @MockBean
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("예약을 조회한다.")
    @Test
    void should_get_reservations() {
        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200).extract();
    }

    @DisplayName("예약을 검색한다.")
    @Test
    void should_search_reservations() {
        MemberLoginRequest loginRequest = new MemberLoginRequest("2222", "pobi@email.com");

        String cookie = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().statusCode(200)
                .extract().header("Set-Cookie");

        RestAssured.given().log().all()
                .cookie(cookie)
                .when().get("/admin/reservations?themeId=1&memberId=1&dateFrom=2024-05-05&dateTo=2024-05-10")
                .then().log().all()
                .statusCode(200).extract();
    }

    @DisplayName("사용자가 예약을 추가할 수 있다.")
    @Test
    void should_insert_reservation_whenT_member_request() {
        doNothing().when(paymentService).confirmReservationPayments(any(ReservationRequest.class));

        MemberLoginRequest loginRequest = new MemberLoginRequest("1234", "otter@email.com");

        String cookie = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().statusCode(200)
                .extract().header("Set-Cookie");

        ReservationRequest request = new ReservationRequest(
                LocalDate.of(2030, 8, 5), 6L, 10L,
                "asdfsdf", "dfadf", 1999999L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie(cookie)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/7");
    }

    @DisplayName("관리자가 예약을 추가할 수 있다.")
    @Test
    void should_insert_reservation_when_admin_request() {
        MemberLoginRequest loginRequest = new MemberLoginRequest("2222", "pobi@email.com");

        String cookie = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().statusCode(200)
                .extract().header("Set-Cookie");

        AdminReservationRequest request = new AdminReservationRequest(
                LocalDate.of(2030, 8, 5), 10L, 6L, 1L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie(cookie)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/7");
    }

    @DisplayName("예약을 추가할 수 있다.")
    @Test
    void should_add_reservation_when_admin_request() {
        doNothing().when(paymentService).confirmReservationPayments(any(ReservationRequest.class));

        MemberLoginRequest loginRequest = new MemberLoginRequest("1234", "otter@email.com");

        String cookie = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().statusCode(200)
                .extract().header("Set-Cookie");

        ReservationRequest request = new ReservationRequest(
                LocalDate.of(2030, 8, 5), 6L, 10L,
                "asdfsdf", "dfadf", 1999999L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .cookie(cookie)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/reservations/7");
    }

    @DisplayName("존재하는 예약이라면 예약을 삭제할 수 있다.")
    @Test
    void should_delete_reservation_when_reservation_exist() {
        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("컨트롤러에 JdbcTemplate 필드가 존재하지 않는다.")
    @Test
    void should_not_exist_JdbcTemplate_field() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }

        AssertionsForClassTypes.assertThat(isJdbcTemplateInjected).isFalse();
    }

    @DisplayName("로그인 정보에 따른 예약 내역을 조회한다.")
    @Test
    void should_find_member_reservation() {
        MemberLoginRequest loginRequest = new MemberLoginRequest("1234", "otter@email.com");

        String cookie = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().statusCode(200)
                .extract().header("Set-Cookie");

        RestAssured.given().log().all()
                .cookie(cookie)
                .when().get("/reservations-mine")
                .then().log().all()
                .statusCode(200);
    }
}
