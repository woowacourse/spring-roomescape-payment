package roomescape.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static roomescape.auth.AuthApiTest.TOKEN_COOKIE_NAME;
import static roomescape.fixture.ServerClientFixture.BASE_URL;
import static roomescape.fixture.ServerClientFixture.MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
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
@ExtendWith(RestDocumentationExtension.class)
public class ReservationApiTest {

    private static final String AUTH_COOKIE_NAME = "token";

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation).operationPreprocessors()
                        .withRequestDefaults(modifyHeaders().remove("Foo"), prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @DisplayName("예약 생성 API 테스트")
    @Nested
    class CreateReservationTest {

        private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
        private static final CreateReservationWithPaymentRequest REQUEST = new CreateReservationWithPaymentRequest(
                TOMORROW, 1L, 1L, "payment_key", "order_id", 1000L);
        private static String TOKEN;

        PaymentsConfirmResponse expectedResponse = new PaymentsConfirmResponse("aaa", "orderId", 1000L);

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
            RestAssured.given(spec).log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .body(REQUEST)
                    .filter(document(
                            "create-reservation",
                            requestCookies(
                                    cookieWithName(TOKEN_COOKIE_NAME).description("인증 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("date").description("예약 날짜"),
                                    fieldWithPath("themeId").description("테마 ID"),
                                    fieldWithPath("timeId").description("예약 시간 ID"),
                                    fieldWithPath("paymentKey").description("결제 고유 키"),
                                    fieldWithPath("orderId").description("주문 ID"),
                                    fieldWithPath("amount").description("금액")
                            ),
                            responseFields(
                                    fieldWithPath("id").description("예약 ID"),
                                    fieldWithPath("member.id").description("예약자 아이디"),
                                    fieldWithPath("member.email").description("예약자 이메일"),
                                    fieldWithPath("member.name").description("예약자 이름"),
                                    fieldWithPath("date").description("예약 날짜"),
                                    fieldWithPath("time.id").description("예약 시간 ID"),
                                    fieldWithPath("time.startAt").description("예약 시작 시간"),
                                    fieldWithPath("theme.id").description("테마 ID"),
                                    fieldWithPath("theme.name").description("테마 이름"),
                                    fieldWithPath("theme.description").description("테마 설명"),
                                    fieldWithPath("theme.thumbnail").description("테마 썸네일")
                            )
                    ))
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
            RestAssured.given(spec).log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .body(REQUEST)
                    .filter(document(
                            "admin/create-reservation",
                            requestCookies(
                                    cookieWithName(TOKEN_COOKIE_NAME).description("인증 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("date").description("이메일"),
                                    fieldWithPath("themeId").description("테마 ID"),
                                    fieldWithPath("timeId").description("예약 시간 ID"),
                                    fieldWithPath("memberId").description("회원 ID")
                            ),
                            responseFields(
                                    fieldWithPath("id").description("예약 ID"),
                                    fieldWithPath("member.id").description("예약자 아이디"),
                                    fieldWithPath("member.email").description("예약자 이메일"),
                                    fieldWithPath("member.name").description("예약자 이름"),
                                    fieldWithPath("date").description("예약자 날짜"),
                                    fieldWithPath("time.id").description("예약 시간 ID"),
                                    fieldWithPath("time.startAt").description("예약 시작 시간"),
                                    fieldWithPath("theme.id").description("테마 ID"),
                                    fieldWithPath("theme.name").description("테마 이름"),
                                    fieldWithPath("theme.description").description("테마 설명"),
                                    fieldWithPath("theme.thumbnail").description("테마 썸네일")
                            )
                    ))
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

        @Autowired
        private MockRestServiceServer server;

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
        void testFindAllMyReservations() throws JsonProcessingException {
            // given
            PaymentsConfirmResponse expectedResponse = new PaymentsConfirmResponse("aaa", "orderId", 1000L);

            server.reset();
            server.expect(requestTo(BASE_URL + "/confirm"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withStatus(HttpStatus.OK)
                            .body(MAPPER.writeValueAsString(expectedResponse))
                            .contentType(MediaType.APPLICATION_JSON));

            String TOKEN = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .extract().cookie(AUTH_COOKIE_NAME);

            final LocalDate TOMORROW = LocalDate.now().plusDays(1);
            final CreateReservationWithPaymentRequest REQUEST = new CreateReservationWithPaymentRequest(
                    TOMORROW, 1L, 1L, "payment_key", "order_id", 1000L);

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .body(REQUEST)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(201);
            // when
            // then
            RestAssured.given(spec).log().all()
                    .cookie(AUTH_COOKIE_NAME, TOKEN)
                    .filter(document(
                            "get-my-reservations",
                            requestCookies(
                                    cookieWithName(AUTH_COOKIE_NAME).description("인증 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("[].id").description("예약 ID"),
                                    fieldWithPath("[].theme").description("테마 이름"),
                                    fieldWithPath("[].date").description("예약 날짜"),
                                    fieldWithPath("[].time").description("예약 시간 ID"),
                                    fieldWithPath("[].status.type").description("예약 상태 타입"),
                                    fieldWithPath("[].status.rank").description("예약 우선 순위"),
                                    fieldWithPath("[].paymentKey").description("결제 고유 키"),
                                    fieldWithPath("[].amount").description("결제 금액")
                            )
                    ))
                    .when().get("/me/reservations")
                    .then().log().all()
                    .statusCode(200)
                    .body("size()", Matchers.is(1));
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
            RestAssured.given(spec).log().all()
                    .cookie(AUTH_COOKIE_NAME, ADMIN_TOKEN)
                    .filter(document(
                            "delete-reservation",
                            requestCookies(
                                    cookieWithName(AUTH_COOKIE_NAME).description("인증 토큰")
                            )
                    ))
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
