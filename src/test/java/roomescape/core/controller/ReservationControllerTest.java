package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.core.domain.ReservationTime;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.dto.waiting.MemberWaitingRequest;
import roomescape.utils.AccessTokenGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.DocumentHelper;
import roomescape.utils.TestFixture;

@AcceptanceTest
class ReservationControllerTest {
    private static final String TOMORROW = TestFixture.getTomorrowDate();
    private static final String DAY_AFTER_TOMORROW = TestFixture.getDayAfterTomorrowDate();
    private static final String RESERVATION_IS_NOT_YOURS_EXCEPTION_MESSAGE = "본인의 예약만 취소할 수 있습니다.";

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestFixture testFixture;

    private String accessToken;

    private RequestSpecification specification;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        specification = DocumentHelper.specification(restDocumentation);

        databaseCleaner.executeTruncate();
        testFixture.initTestData();

        accessToken = AccessTokenGenerator.adminTokenGenerate();
    }

    @Test
    @DisplayName("예약을 생성할 수 있다.")
    void createReservation() {
        ReservationPaymentRequest request = new ReservationPaymentRequest(TOMORROW, 1L, 1L, "", "", 1000);

        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .filter(document("reservation-create",
                        requestFields(fieldWithPath("date").description("예약할 날짜"),
                                fieldWithPath("timeId").description("예약할 시간"),
                                fieldWithPath("themeId").description("예약할 테마"),
                                fieldWithPath("paymentKey").description("결제 키"),
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("amount").description("결제 금액")),
                        responseFields(fieldWithPath("id").description("예약 ID"),
                                fieldWithPath("member").description("예약한 사용자"),
                                fieldWithPath("member.id").description("예약한 사용자 ID"),
                                fieldWithPath("member.name").description("예약한 사용자 이름"),
                                fieldWithPath("date").description("예약된 날짜"),
                                fieldWithPath("time").description("예약된 시간"),
                                fieldWithPath("time.id").description("예약된 시간 ID"),
                                fieldWithPath("time.startAt").description("예약된 시간 값"),
                                fieldWithPath("theme").description("예약된 테마"),
                                fieldWithPath("theme.id").description("예약된 테마 ID"),
                                fieldWithPath("theme.name").description("예약된 테마 이름"),
                                fieldWithPath("theme.description").description("예약된 테마 설명"),
                                fieldWithPath("theme.thumbnail").description("예약된 테마 이미지"))))
                .body(request)
                .when().post("/waitings")
                .then().assertThat()
                .statusCode(is(201));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "abc"})
    @DisplayName("예약 생성 시, date의 형식이 올바르지 않으면 예외가 발생한다.")
    void validateReservationWithDateFormat(final String date) {
        ReservationPaymentRequest request = new ReservationPaymentRequest(date, 1L, 1L, "", "", 1000);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, date가 이미 지난 날짜면 예외가 발생한다.")
    void validateReservationWithPastDate() {
        ReservationPaymentRequest request = new ReservationPaymentRequest("2020-10-10", 1L, 1L, "", "", 1000);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, date는 오늘이고 time은 이미 지난 시간이면 예외가 발생한다.")
    void validateReservationWithTodayPastTime() {
        final ReservationTime pastTime = testFixture.persistReservationTimeBeforeMinute(1);

        ReservationPaymentRequest request = new ReservationPaymentRequest(
                LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                pastTime.getId(), 1L, "", "", 1000);

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
        ReservationPaymentRequest request = new ReservationPaymentRequest(TOMORROW, null, 1L, "", "", 1000);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, timeId 값으로 찾을 수 있는 시간이 없으면 예외가 발생한다.")
    void validateReservationWithTimeIdNotFound() {
        ReservationPaymentRequest request = new ReservationPaymentRequest(TOMORROW, 0L, 1L, "", "", 1000);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, 해당 날짜와 시간에 예약 내역이 있으면 예외가 발생한다.")
    void validateReservationWithDuplicatedDateAndTime() {
        ReservationPaymentRequest request = new ReservationPaymentRequest(TOMORROW, 1L, 1L, "", "", 1000);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

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
        ReservationPaymentRequest request = new ReservationPaymentRequest(TOMORROW, 1L, null, "", "", 1000);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 생성 시, themeId 값으로 찾을 수 있는 테마가 없으면 예외가 발생한다.")
    void validateReservationWithThemeIdNotFound() {
        ReservationPaymentRequest request = new ReservationPaymentRequest(TOMORROW, 1L, 0L, "", "", 1000);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("모든 예약 내역을 조회한다.")
    void findAllReservations() {
        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(document("reservations",
                        responseFields(fieldWithPath("[].id").description("예약 ID"),
                                fieldWithPath("[].date").description("예약한 날짜"),
                                fieldWithPath("[].member").description("예약한 사용자"),
                                fieldWithPath("[].member.id").description("예약한 사용자 ID"),
                                fieldWithPath("[].member.name").description("예약한 사용자 이름"),
                                fieldWithPath("[].time").description("예약된 시간"),
                                fieldWithPath("[].time.id").description("예약된 시간 ID"),
                                fieldWithPath("[].time.startAt").description("예약된 시간 값"),
                                fieldWithPath("[].theme").description("예약된 테마"),
                                fieldWithPath("[].theme.id").description("예약된 테마 ID"),
                                fieldWithPath("[].theme.name").description("예약된 테마 이름"),
                                fieldWithPath("[].theme.description").description("예약된 테마 설명"),
                                fieldWithPath("[].theme.thumbnail").description("예약된 테마 이미지"))))
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    @DisplayName("예약을 삭제한다.")
    void validateReservationDelete() {
        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .filter(document("reservation-delete", pathParameters(
                        parameterWithName("id").description("취소하려는 예약 ID"))))
                .when().delete("/reservations/{id}", 1L)
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("조건에 따라 예약을 조회한다.")
    void findReservationsByCondition() {
        testFixture.persistReservationWithDateAndTimeAndTheme(TOMORROW, 1L, 1L);
        testFixture.persistReservationWithDateAndTimeAndTheme(DAY_AFTER_TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .queryParams(
                        "memberId", 1L,
                        "themeId", 1L,
                        "dateFrom", TOMORROW,
                        "dateTo", TOMORROW)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .queryParams(
                        "memberId", 1L,
                        "themeId", 1L,
                        "dateFrom", TOMORROW,
                        "dateTo", DAY_AFTER_TOMORROW)
                .filter(document("reservations-condition",
                        responseFields(fieldWithPath("[].id").description("예약 ID"),
                                fieldWithPath("[].date").description("예약한 날짜"),
                                fieldWithPath("[].member").description("예약한 사용자"),
                                fieldWithPath("[].member.id").description("예약한 사용자 ID"),
                                fieldWithPath("[].member.name").description("예약한 사용자 이름"),
                                fieldWithPath("[].time").description("예약된 시간"),
                                fieldWithPath("[].time.id").description("예약된 시간 ID"),
                                fieldWithPath("[].time.startAt").description("예약된 시간 값"),
                                fieldWithPath("[].theme").description("예약된 테마"),
                                fieldWithPath("[].theme.id").description("예약된 테마 ID"),
                                fieldWithPath("[].theme.name").description("예약된 테마 이름"),
                                fieldWithPath("[].theme.description").description("예약된 테마 설명"),
                                fieldWithPath("[].theme.thumbnail").description("예약된 테마 이미지"))))
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    @DisplayName("토큰이 유효하지 않을 경우 예외가 발생한다.")
    void validateToken() {
        ReservationPaymentRequest request = new ReservationPaymentRequest(TOMORROW, 1L, 1L, "", "", 1000);

        RestAssured.given().log().all()
                .cookies("token", "invalid-token")
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("현재 로그인된 회원의 예약 대기를 포함한 예약 목록을 조회한다.")
    void findLoginMemberReservation() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .filter(document("reservations-mine",
                        responseFields(fieldWithPath("[].id").description("예약 ID"),
                                fieldWithPath("[].date").description("예약한 날짜"),
                                fieldWithPath("[].theme").description("예약한 테마"),
                                fieldWithPath("[].time").description("예약한 시간"),
                                fieldWithPath("[].status").description("예약 상태"),
                                fieldWithPath("[].paymentKey").description("결제 키").optional(),
                                fieldWithPath("[].amount").description("결제 금액").optional())))
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    @DisplayName("내 예약이 아닌 다른 회원의 예약을 삭제하면 예외가 발생한다.")
    void validateDeleteOtherMemberReservation() {
        testFixture.persistReservationWithDateAndTimeAndTheme(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.memberTokenGenerate())
                .when().delete("/reservations/2")
                .then().log().all()
                .statusCode(400)
                .body("detail", is(RESERVATION_IS_NOT_YOURS_EXCEPTION_MESSAGE));
    }
}