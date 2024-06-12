package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.dto.waiting.MemberWaitingRequest;
import roomescape.utils.AccessTokenGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.DocumentHelper;
import roomescape.utils.TestFixture;

@AcceptanceTest
class WaitingControllerTest {
    public static final String WAITING_IS_NOT_YOURS_EXCEPTION_MESSAGE = "본인의 예약 대기만 취소할 수 있습니다.";
    private static final String TOMORROW = TestFixture.getTomorrowDate();

    private String accessToken;

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestFixture testFixture;

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
    @DisplayName("예약 대기를 생성할 수 있다.")
    void createWaiting() {
        MemberWaitingRequest request = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given(this.specification).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .filter(document("waiting-create",
                        requestCookies(cookieWithName("token").description("사용자 인가 토큰")),
                        requestFields(fieldWithPath("date").description("예약 대기할 날짜"),
                                fieldWithPath("timeId").description("예약 대기할 시간"),
                                fieldWithPath("themeId").description("예약 대기할 테마")),
                        responseFields(fieldWithPath("id").description("예약 대기 ID"),
                                fieldWithPath("member.id").description("예약 대기한 사용자 ID"),
                                fieldWithPath("member.name").description("예약 대기한 사용자 이름"),
                                fieldWithPath("date").description("예약 대기된 날짜"),
                                fieldWithPath("time.id").description("예약 대기된 시간 ID"),
                                fieldWithPath("time.startAt").description("예약 대기된 시간 값"),
                                fieldWithPath("theme.id").description("예약 대기된 테마 ID"),
                                fieldWithPath("theme.name").description("예약 대기된 테마 이름"),
                                fieldWithPath("theme.description").description("예약 대기된 테마 설명"),
                                fieldWithPath("theme.thumbnail").description("예약 대기된 테마 이미지"))))
                .body(request)
                .when().post("/waitings")
                .then().assertThat()
                .statusCode(is(201));
    }

    @Test
    @DisplayName("이미 예약한 내역이 존재하면 예약 대기를 생성할 수 없다.")
    void createWaitingAlreadyHaveReservation() {
        ReservationPaymentRequest request = new ReservationPaymentRequest(TOMORROW, 1L, 1L, "paymentKey", "orderId",
                1000);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("이미 예약 대기한 내역이 존재하면 예약 대기를 생성할 수 없다.")
    void createDuplicateWaitingBySameMember() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 대기를 취소할 수 있다.")
    void deleteWaiting() {
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
                .filter(document("waiting-delete",
                        requestCookies(cookieWithName("token").description("사용자 인가 토큰")),
                        pathParameters(parameterWithName("id").description("취소하려는 예약 대기 ID"))))
                .when().delete("/waitings/{id}", 1L)
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("전체 예약 대기 목록을 조회할 수 있다.")
    void findAllWaitings() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given(this.specification).log().all()
                .accept("application/json")
                .filter(document("waitings",
                        responseFields(fieldWithPath("[].id").description("예약 대기 ID"),
                                fieldWithPath("[].member.id").description("예약 대기한 사용자 ID"),
                                fieldWithPath("[].member.name").description("예약 대기한 사용자 이름"),
                                fieldWithPath("[].date").description("예약 대기된 날짜"),
                                fieldWithPath("[].time.id").description("예약 대기된 시간 ID"),
                                fieldWithPath("[].time.startAt").description("예약 대기된 시간 값"),
                                fieldWithPath("[].theme.id").description("예약 대기된 테마 ID"),
                                fieldWithPath("[].theme.name").description("예약 대기된 테마 이름"),
                                fieldWithPath("[].theme.description").description("예약 대기된 테마 설명"),
                                fieldWithPath("[].theme.thumbnail").description("예약 대기된 테마 이미지"))))
                .when().get("/waitings")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    @DisplayName("내 예약 대기가 아닌 다른 회원의 예약 대기를 삭제하면 예외가 발생한다.")
    void deleteWaitingByOtherMember() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.memberTokenGenerate())
                .when().delete("/waitings/1")
                .then().log().all()
                .statusCode(400)
                .body("detail", is(WAITING_IS_NOT_YOURS_EXCEPTION_MESSAGE));
    }
}