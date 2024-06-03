package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.dto.waiting.MemberWaitingRequest;
import roomescape.utils.AccessTokenGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.TestFixture;

@AcceptanceTest
class ReservationControllerTest {
    private static final String TOMORROW = TestFixture.getTomorrowDate();
    private static final String DAY_AFTER_TOMORROW = TestFixture.getDayAfterTomorrowDate();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestFixture testFixture;

    private String accessToken;
    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        databaseCleaner.executeTruncate();
        testFixture.initTestData();

        accessToken = AccessTokenGenerator.adminTokenGenerate();
        spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("돈을 지불하며 예약을 생성한다.")
    void createAndPay() {
        ReservationPaymentRequest request
                = new ReservationPaymentRequest(TOMORROW, 1L, 1L, "1", "1", 1L);

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .accept("application/json")
                .filter(document("reservations/mine/post/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("로그인한 회원의 토큰")),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 시간 id"),
                                fieldWithPath("themeId").description("예약 테마 id"),
                                fieldWithPath("paymentKey").description("결제 키"),
                                fieldWithPath("orderId").description("주문 id"),
                                fieldWithPath("amount").description("결제 금액")
                        ),
                        responseFields(
                                fieldWithPath("reservationResponse.*.*").description("예약 정보"),
                                fieldWithPath("reservationResponse.*").description("예약 정보"),
                                fieldWithPath("paymentResponse.*").description("결제 정보")
                        )))
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("모든 예약 내역을 조회한다.")
    void findAllReservations() {
        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(document("reservations/get/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("예약 id"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].member.*").description("예약자 정보"),
                                fieldWithPath("[].time.*").description("예약 시간 정보"),
                                fieldWithPath("[].theme.*").description("예약 테마 정보")
                        )))
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    @DisplayName("예약을 삭제한다.")
    void validateReservationDelete() {
        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(document("reservations/delete/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("로그인한 회원의 토큰")),
                        pathParameters(parameterWithName("id").description("삭제할 예약의 id"))))
                .when().delete("/reservations/{id}", 1)
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("조건에 따라 예약을 조회한다.")
    void findReservationsByCondition() {
        testFixture.persistReservationWithDateAndTimeAndTheme(TOMORROW, 1L, 1L);
        testFixture.persistReservationWithDateAndTimeAndTheme(DAY_AFTER_TOMORROW, 1L, 1L);

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .queryParams(
                        "memberId", 1L,
                        "themeId", 1L,
                        "dateFrom", TOMORROW,
                        "dateTo", TOMORROW
                )
                .accept("application/json")
                .filter(document("reservations/filter/get",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        queryParameters(
                                parameterWithName("memberId").description("조회할 멤버의 id"),
                                parameterWithName("themeId").description("조회할 테마 id"),
                                parameterWithName("dateFrom").description("조회할 첫 날짜"),
                                parameterWithName("dateTo").description("조회할 마지막 날짜")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("예약 id"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].member.*").description("예약자 정보"),
                                fieldWithPath("[].time.*").description("예약 시간 정보"),
                                fieldWithPath("[].theme.*").description("예약 테마 정보")
                        )))
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .queryParams(
                        "memberId", 1L,
                        "themeId", 1L,
                        "dateFrom", TOMORROW,
                        "dateTo", DAY_AFTER_TOMORROW
                )
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    @DisplayName("현재 로그인된 회원의 예약 대기를 포함한 예약 목록을 조회한다.")
    void findLoginMemberReservation() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(document("reservations/mine/get/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("로그인한 회원의 토큰")),
                        responseFields(
                                fieldWithPath("[].id").description("예약 id"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].time").description("예약 시간"),
                                fieldWithPath("[].theme").description("예약 테마 이름"),
                                fieldWithPath("[].status").description("상태(예약 or 몇 번째 대기인지"),
                                fieldWithPath("[].paymentKey").description("결제 키(nullable)"),
                                fieldWithPath("[].amount").description("결제 금액(nullable)")
                        )))
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "abc"})
    @DisplayName("예약 생성 시, date의 형식이 올바르지 않으면 예외가 발생한다.")
    void validateReservationWithDateFormat(final String date) {
        ReservationPaymentRequest request = new ReservationPaymentRequest(date, 1L, 1L, "", "", 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, timeId가 null이면 예외가 발생한다.")
    void validateReservationWithNullTimeId() {
        ReservationPaymentRequest request
                = new ReservationPaymentRequest(TOMORROW, null, 1L, "", "", 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, themeId가 null이면 예외가 발생한다.")
    void validateReservationWithNullThemeId() {
        ReservationPaymentRequest request
                = new ReservationPaymentRequest(TOMORROW, 1L, null, "", "", 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("토큰이 유효하지 않을 경우 예외가 발생한다.")
    void validateToken() {
        ReservationPaymentRequest request
                = new ReservationPaymentRequest(TOMORROW, 1L, 1L, "", "", 1L);

        RestAssured.given().log().all()
                .cookies("token", "invalid-token")
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("내 예약이 아닌 다른 회원의 예약을 삭제하면 예외가 발생한다.")
    void validateDeleteOtherMemberReservation() {
        testFixture.persistReservationWithDateAndTimeAndTheme(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.memberTokenGenerate())
                .when().delete("/reservations/2")
                .then().log().all()
                .statusCode(400);
    }
}
