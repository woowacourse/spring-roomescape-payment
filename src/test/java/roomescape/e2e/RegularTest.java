package roomescape.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.fixture.IntegrationFixture.FUTURE_DATE_TEXT;
import static roomescape.fixture.IntegrationFixture.PASSWORD;
import static roomescape.fixture.IntegrationFixture.REGULAR2_EMAIL;
import static roomescape.fixture.IntegrationFixture.REGULAR_EMAIL;
import static roomescape.fixture.IntegrationFixture.TOKEN;
import static roomescape.fixture.IntegrationFixture.createRegularReservation;
import static roomescape.fixture.IntegrationFixture.createReservationTime;
import static roomescape.fixture.IntegrationFixture.createTheme;
import static roomescape.fixture.IntegrationFixture.findReservation;
import static roomescape.fixture.IntegrationFixture.loginAndGetAuthToken;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.security.dto.request.LoginRequest;
import roomescape.payment.application.dto.PaymentGatewayRequest;
import roomescape.payment.application.dto.PaymentGatewayResponse;
import roomescape.payment.domain.PaymentGateway;
import roomescape.payment.domain.PaymentType;
import roomescape.payment.presentation.dto.request.PaymentRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;
import roomescape.reservation.application.event.TestEventPublisher;
import roomescape.reservationslot.presentation.dto.response.MyReservationResponse;
import roomescape.reservationslot.presentation.dto.response.ReservationResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
@ExtendWith(RestDocumentationExtension.class)
public class RegularTest {

    @MockitoBean
    private PaymentGateway paymentGateway;

    @Autowired
    private TestEventPublisher eventPublisher;

    @LocalServerPort
    private int port;

    private RequestSpecification spec;
    private String regularToken;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(provider))
                .build();
        regularToken = loginAndGetAuthToken(REGULAR_EMAIL, PASSWORD);
    }

    @Test
    void login() {
        RestAssured.given(spec).log().all()
                .filter(document("member/회원-로그인",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        )))
                .body(new LoginRequest(REGULAR_EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie(TOKEN);
    }

    @Test
    void logout() {
        // when
        RestAssured.given(spec).log().all()
                .filter(document("member/회원-로그아웃"))
                .body(new LoginRequest(REGULAR_EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(TOKEN, regularToken)
                .when().post("/logout")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void createReservation() {
        createReservationTime();
        createTheme("추리");
        String authToken = loginAndGetAuthToken(REGULAR_EMAIL, PASSWORD);

        // when
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", FUTURE_DATE_TEXT);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);

        RestAssured.given(spec).log().all()
                .filter(document(
                        "reservation/회원-예약-생성",
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("시간 id"),
                                fieldWithPath("themeId").description("테마 id")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(reservation)
                .cookie(TOKEN, authToken)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        // then
        findReservation();
    }

    @Test
    void responseUnAuthorizedWhenRegularAccessAdminPage() {
        // when
        RestAssured.given().log().all()
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    void responseForbiddenWhenRegularAccessAdminPage() {
        // when
        RestAssured.given().log().all()
                .cookie(TOKEN, regularToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    void createWaitingReservations() {
        // given
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L);

        // when
        String user2Token = loginAndGetAuthToken(REGULAR2_EMAIL, PASSWORD);
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", FUTURE_DATE_TEXT);
        reservation.put("timeId", 1L);
        reservation.put("themeId", 1L);

        // when
        ReservationResponse reservationResponse = RestAssured.given(spec).log().all()
                .filter(document(
                        "waiting-reservation/회원-대기예약-생성",
                        requestFields(
                                fieldWithPath("date").description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("timeId").description("예약 시간 ID"),
                                fieldWithPath("themeId").description("테마 ID")
                        ),
                        responseFields(
                                fieldWithPath("reservationSlotId").description("예약 슬롯 ID"),
                                fieldWithPath("waitingId").description("대기 ID")
                        )
                ))
                .contentType(ContentType.JSON)
                .cookie(TOKEN, user2Token)
                .body(reservation)
                .when().post("/waiting-reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(ReservationResponse.class);

        // then
        assertThat(reservationResponse).isNotNull();
    }

    @Test
    void findMyReservations() {
        // given
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L);
        String userToken = loginAndGetAuthToken(REGULAR_EMAIL, PASSWORD);

        // when
        List<MyReservationResponse> responses = RestAssured.given(spec).log().all()
                .filter(document(
                        "reservation/회원-내예약목록-조회",
                        responseFields(
                                fieldWithPath("[].theme").description("테마 이름"),
                                fieldWithPath("[].date").description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("[].time").description("예약 시간"),
                                fieldWithPath("[].isReserved").description("예약 완료 여부"),
                                fieldWithPath("[].waitingRank").description("대기 순번 (예약 확정 시 0)"),
                                fieldWithPath("[].paymentKey").description("결제 키 (nullable)"),
                                fieldWithPath("[].amount").description("결제 금액"),
                                fieldWithPath("[].reservationSlotId").description("예약 슬롯 ID")
                        )
                ))
                .cookie(TOKEN, userToken)
                .when().get("/my-reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(responses.size()).isEqualTo(1);
            softAssertions.assertThat(responses.getFirst().isReserved()).isTrue();
        });
    }

    @Test
    void removeWaitingReservations() {
        // given
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L);

        String user2Token = loginAndGetAuthToken(REGULAR2_EMAIL, PASSWORD);
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", FUTURE_DATE_TEXT);
        reservation.put("timeId", 1L);
        reservation.put("themeId", 1L);

        ReservationResponse reservationResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, user2Token)
                .body(reservation)
                .when().post("/waiting-reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .as(ReservationResponse.class);

        // when
        Long reservationSlotId = reservationResponse.reservationSlotId();

        RestAssured.given(spec).log().all()
                .filter(document(
                        "waiting-reservation/회원-대기예약-삭제",
                        pathParameters(
                                parameterWithName("reservationSlotId").description("삭제할 예약 슬롯 ID")
                        )
                ))
                .contentType(ContentType.JSON)
                .cookie(TOKEN, user2Token)
                .pathParam("reservationSlotId", reservationSlotId)
                .when().delete("/waiting-reservations/{reservationSlotId}")
                .then().log().all()
                .statusCode(204);

        // then
        List<MyReservationResponse> responses = RestAssured.given().log().all()
                .cookie(TOKEN, user2Token)
                .when().get("/my-reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        assertThat(responses).isEmpty();
    }

    @Test
    void approvePaymentTest() {
        // given
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L);
        long reservationId = findReservation();
        String paymentKey = "PAYMENT_KEY";
        String orderId = "ORDER_ID";
        long amount = 5000L;

        // when & then
        PaymentRequest paymentRequest = new PaymentRequest(paymentKey, orderId, amount, PaymentType.NORMAL,
                reservationId);
        PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse(paymentKey, orderId, amount);
        Mockito.when(paymentGateway.approvePayment(Mockito.any(PaymentGatewayRequest.class)))
                .thenReturn(paymentGatewayResponse);

        RestAssured.given(spec).log().all()
                .filter(document(
                        "payment/회원-결제승인",
                        requestFields(
                                fieldWithPath("paymentKey").description("결제 키"),
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("amount").description("결제 금액"),
                                fieldWithPath("reservationId").description("예약 ID"),
                                fieldWithPath("paymentType").description("결제 타입")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("totalAmount").description("결제 금액")
                        )
                ))
                .cookie(TOKEN, regularToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(paymentRequest)
                .when().post("/payments/approve")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(PaymentApproveResponse.class);
    }
}
