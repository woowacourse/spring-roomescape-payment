package roomescape.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static roomescape.fixture.ServerClientFixture.BASE_URL;
import static roomescape.fixture.ServerClientFixture.MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.TestClientConfig;
import roomescape.auth.dto.LoginRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.reservation.dto.CreateReservationWithMemberRequest;
import roomescape.reservation.dto.CreateReservationWithPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/test-time-data.sql", "/test-theme-data.sql", "/test-member-data.sql"})
@Import(TestClientConfig.class)
public class ReservationApiTest {

    private static final String AUTH_COOKIE_NAME = "token";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("예약 생성 API 테스트")
    @Nested
    class CreateReservationTest {

        private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
        private static final CreateReservationWithPaymentRequest REQUEST = new CreateReservationWithPaymentRequest(
                TOMORROW, 1L, 1L, "payment_key", "order_id", 1000L);
        private static String TOKEN;

        PaymentsConfirmResponse expectedResponse = new PaymentsConfirmResponse("aaa", 1000L);

        @Autowired
        private MockRestServiceServer server;

        @BeforeEach
        void setUp() throws JsonProcessingException {
            TOKEN = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .extract().cookie(AUTH_COOKIE_NAME);
            server.reset();
            server.expect(requestTo(BASE_URL + "/confirm"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withStatus(HttpStatus.OK)
                            .body(MAPPER.writeValueAsString(expectedResponse))
                            .contentType(MediaType.APPLICATION_JSON));
        }

        @DisplayName("예약 생성을 성공할 경우 201을 반환한다.")
        @Test
        void testCreateReservation() {
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .body(REQUEST)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(201)
                    .body("id", Matchers.equalTo(1))
                    .body("member.name", Matchers.equalTo("사용자1"));
        }

        @DisplayName("쿠키 정보가 올바르지 않을 경우 401을 반환한다.")
        @Test
        void testInvalidCookie() {
            // 쿠키 없음
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(REQUEST)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(401);
            // JWT 토큰 파싱 불가능
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, "invalidValue")
                    .body(REQUEST)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("중복 예약을 생성할 경우 400을 반환한다.")
        @Test
        void testDuplicateReservation() throws JsonProcessingException {
            // given
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .body(REQUEST)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(201);

            server.reset();
            server.expect(requestTo(BASE_URL + "/confirm"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withStatus(HttpStatus.OK)
                            .body(MAPPER.writeValueAsString(expectedResponse))
                            .contentType(MediaType.APPLICATION_JSON));
            // when
            // then
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .body(REQUEST)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(400);
        }
    }

    @DisplayName("관리자 예약 생성 API 테스트")
    @Nested
    class AdminCreateReservationTest {

        private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
        private static final CreateReservationWithMemberRequest REQUEST = new CreateReservationWithMemberRequest(
                TOMORROW, 1L, 1L, 1L);
        private static String TOKEN;

        @BeforeEach
        void setUp() {
            TOKEN = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("admin@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .extract().cookie(AUTH_COOKIE_NAME);
        }

        @DisplayName("예약 생성을 성공할 경우 201을 반환한다.")
        @Test
        void testCreateReservation() {
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .body(REQUEST)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(201)
                    .body("id", Matchers.equalTo(1))
                    .body("member.name", Matchers.equalTo("사용자1"));
        }

        @DisplayName("쿠키 정보가 올바르지 않을 경우 401을 반환한다.")
        @Test
        void testInvalidCookie() {
            // 쿠키 없음
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(REQUEST)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(401);
            // JWT 토큰 파싱 불가능
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, "invalidValue")
                    .body(REQUEST)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("중복 예약을 생성할 경우 400을 반환한다.")
        @Test
        void testDuplicatedReservation() {
            // given
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .body(REQUEST)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(201);
            // when
            // then
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .body(REQUEST)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(400);
        }
    }

    @DisplayName("내 예약 조회 API 테스트")
    @Nested
    class MyReservationsTest {

        @DisplayName("쿠키 정보가 올바르지 않을 경우 401을 반환한다.")
        @Test
        void testInvalidCookie() {
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .when().get("/me/reservations")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("내 예약 조회를 성공할 경우 200을 반환한다.")
        @Test
        void testFindAllMyReservations() {
            // given
            String TOKEN = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .extract().cookie(AUTH_COOKIE_NAME);
            // when
            // then
            RestAssured.given().log().all()
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .when().get("/me/reservations")
                    .then().log().all()
                    .statusCode(200)
                    .body("size()", Matchers.is(0));
        }
    }

    @DisplayName("예약 취소 API 테스트")
    @Nested
    class DeleteReservationTest {

        public static final int RESERVATION_ID_OF_MEMBER_1 = 3;
        private static String MEMBER_1_TOKEN;
        private static String ADMIN_TOKEN;

        @BeforeEach
        void setUp() {
            MEMBER_1_TOKEN = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .extract().cookie(AUTH_COOKIE_NAME);
            ADMIN_TOKEN = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("admin@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(200)
                    .extract().cookie(AUTH_COOKIE_NAME);
        }

        @DisplayName("예약을 취소할 경우 대기 목록을 자동 업데이트 한다.")
        @Test
        @Sql({"/test-time-data.sql", "/test-theme-data.sql", "/test-member-data.sql", "/test-waiting-data.sql"})
        void testUpdateWaiting() {
            // given
            // when
            RestAssured.given().log().all()
                    .cookie(AUTH_COOKIE_NAME, ADMIN_TOKEN)
                    .when().delete("/admin/reservations/{id}", RESERVATION_ID_OF_MEMBER_1)
                    .then().log().all()
                    .statusCode(204);
            // then
            ReservationResponse[] responses = RestAssured.given().log().all()
                    .cookie(AUTH_COOKIE_NAME, ADMIN_TOKEN)
                    .when().get("/reservations")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(ReservationResponse[].class);
            assertAll(
                    () -> assertThat(responses.length).isEqualTo(3), // 예약이 삭제되지 않고 대기와 교체됨
                    () -> assertThat(responses[2].member()).isEqualTo(responses[1].member()) // 예약자 1에서 예약자 2로 바뀜
            );
        }
    }
}
