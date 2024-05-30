package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.dto.waiting.MemberWaitingRequest;
import roomescape.infrastructure.PaymentProvider;
import roomescape.utils.AccessTokenGenerator;
import roomescape.utils.DatabaseCleaner;
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

    @MockBean
    private PaymentProvider paymentProvider;

    @PostConstruct
    public void init() {
        when(paymentProvider.getPaymentConfirmResponse(any()))
                .thenReturn(new PaymentConfirmResponse(1000, "orderId", "paymentKey"));
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        databaseCleaner.executeTruncate();
        testFixture.initTestData();

        accessToken = AccessTokenGenerator.adminTokenGenerate();
    }

    @Test
    @DisplayName("예약 대기를 생성할 수 있다.")
    void createWaiting() {
        MemberWaitingRequest request = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);
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

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .when().delete("/waitings/1")
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

        RestAssured.given().log().all()
                .cookies("token", accessToken)
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