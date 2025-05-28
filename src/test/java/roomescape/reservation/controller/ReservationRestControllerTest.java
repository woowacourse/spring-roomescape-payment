package roomescape.reservation.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import roomescape.auth.jwt.JwtTokenProvider;
import roomescape.payment.processor.toss.TossPaymentConfirmRequest;
import roomescape.payment.processor.toss.TossPaymentConfirmResponse;
import roomescape.payment.processor.toss.TossPaymentProcessor;
import roomescape.reservation.dto.AvailableReservationTimeResponse;
import roomescape.reservation.dto.TossPaymentRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("classpath:data.sql")
class ReservationRestControllerTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ReservationRestController reservationRestController;

    @MockitoBean
    private TossPaymentProcessor tossPaymentProcessor;

    @Test
    void 요청_형식이_맞지_않아_예약_정보_저장에_실패하는_경우_bad_request를_반환한다() {
        //given
        final Map<String, Object> params = createReservationRequestJsonMap(
            "2025 04 15",
            "1",
            "1",
            new TossPaymentRequest("paymentKey", "orderId", 10000)
        );

        //when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 예약_정보를_저장한다() {
        //given
        final String payload = "wooga@gmail.com";
        final String token = jwtTokenProvider.createToken(payload);
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest("paymentKey", "orderId", 10000);
        final TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
            tossPaymentRequest.amount(),
            tossPaymentRequest.orderId(),
            tossPaymentRequest.paymentKey()
        );
        final Map<String, Object> params = createReservationRequestJsonMap(
            "2025-10-15",
            "1",
            "1",
            tossPaymentRequest
        );

        setTossPaymentConfirm(tossPaymentConfirmRequest, null);

        //when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 예약_정보를_삭제한다() {
        //given
        final String payload = "wooga@gmail.com";
        final String token = jwtTokenProvider.createToken(payload);
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest("paymentKey", "orderId", 10000);
        final TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
            tossPaymentRequest.amount(),
            tossPaymentRequest.orderId(),
            tossPaymentRequest.paymentKey()
        );
        final Map<String, Object> params = createReservationRequestJsonMap(
            "2025-10-15",
            "1",
            "1",
            tossPaymentRequest
        );

        setTossPaymentConfirm(tossPaymentConfirmRequest, null);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        //when & then
        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 삭제할_예약_정보가_없는_경우_not_found를_반환한다() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void 예약_정보_목록을_조회한다() {
        //given
        final String payload = "wooga@gmail.com";
        final String token = jwtTokenProvider.createToken(payload);
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest("paymentKey", "orderId", 10000);
        final TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
            tossPaymentRequest.amount(),
            tossPaymentRequest.orderId(),
            tossPaymentRequest.paymentKey()
        );
        final Map<String, Object> params = createReservationRequestJsonMap(
            "2025-10-15",
            "1",
            "1",
            tossPaymentRequest
        );

        setTossPaymentConfirm(tossPaymentConfirmRequest, null);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(1));
    }

    @Test
    void 예약_가능한_시간_목록을_조회한다() {
        //given
        final String payload = "wooga@gmail.com";
        final String token = jwtTokenProvider.createToken(payload);
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest("paymentKey", "orderId", 10000);
        final TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
            tossPaymentRequest.amount(),
            tossPaymentRequest.orderId(),
            tossPaymentRequest.paymentKey()
        );
        final Map<String, Object> params = createReservationRequestJsonMap(
            "2026-04-15",
            "1",
            "1",
            tossPaymentRequest
        );

        setTossPaymentConfirm(tossPaymentConfirmRequest, null);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        final List<AvailableReservationTimeResponse> availableReservationTimeResponses =
                RestAssured.given().log().all()
                        .queryParam("date", "2026-04-15")
                        .queryParam("themeId", "1")
                        .when().get("/reservations/available-times")
                        .then().log().all()
                        .statusCode(HttpStatus.OK.value())
                        .extract().jsonPath()
                        .getList(".", AvailableReservationTimeResponse.class);

        final long count = availableReservationTimeResponses.stream()
                .filter(AvailableReservationTimeResponse::alreadyBooked)
                .count();

        assertThat(count).isEqualTo(1);
    }

    @Test
    void 멤버가_예약한_정보를_조회한다() {
        //given
        final String payload = "wooga@gmail.com";
        final String token = jwtTokenProvider.createToken(payload);
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest("paymentKey", "orderId", 10000);
        final TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
            tossPaymentRequest.amount(),
            tossPaymentRequest.orderId(),
            tossPaymentRequest.paymentKey()
        );
        final Map<String, Object> params = createReservationRequestJsonMap(
            "2026-04-15",
            "1",
            "1",
            tossPaymentRequest
        );

        setTossPaymentConfirm(tossPaymentConfirmRequest, null);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(1));
    }

    @Test
    void 멤버가_예약_대기를_등록한다() {
        //given
        final String payload = "wooga@gmail.com";
        final String token = jwtTokenProvider.createToken(payload);
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest("paymentKey", "orderId", 10000);
        final TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
            tossPaymentRequest.amount(),
            tossPaymentRequest.orderId(),
            tossPaymentRequest.paymentKey()
        );
        final Map<String, Object> reservationParams = createReservationRequestJsonMap(
            "2026-04-15",
            "1",
            "1",
            tossPaymentRequest
        );

        setTossPaymentConfirm(tossPaymentConfirmRequest, null);
        final Map<String, String> waitingParams = createWaitingRequestJsonMap("2026-04-15", "1", "1");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        //when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(waitingParams)
                .when().post("/reservations/waitings")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 예약_대기_아이디_기준으로_삭제한다() {
        //given
        final String payload = "wooga@gmail.com";
        final String token = jwtTokenProvider.createToken(payload);
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest("paymentKey", "orderId", 10000);
        final TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(
            tossPaymentRequest.amount(),
            tossPaymentRequest.orderId(),
            tossPaymentRequest.paymentKey()
        );
        final Map<String, Object> reservationParams = createReservationRequestJsonMap(
            "2026-04-15",
            "1",
            "1",
            tossPaymentRequest
        );

        setTossPaymentConfirm(tossPaymentConfirmRequest, null);
        final Map<String, String> waitingParams = createWaitingRequestJsonMap("2026-04-15", "1", "1");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(waitingParams)
                .when().post("/reservations/waitings")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        //when & then
        RestAssured.given().log().all()
                .when().delete("/reservations/waitings/1")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 컨트롤러는_JdbcTemplate_타입의_필드를_갖고_있지_않다() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationRestController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }

        assertThat(isJdbcTemplateInjected).isFalse();
    }

    private Map<String, String> createWaitingRequestJsonMap(
        final String date,
        final String theme,
        final String time) {
        return Map.of(
                "date", date,
                "theme", theme,
                "time", time
        );
    }


    private Map<String, Object> createReservationRequestJsonMap(
        final String date,
        final String themeId,
        final String timeId,
        final TossPaymentRequest request) {
        return Map.of(
            "date", date,
            "themeId", themeId,
            "timeId", timeId,
            "tossPaymentRequest", Map.of(
                "paymentKey", request.paymentKey(),
                "orderId", request.orderId(),
                "amount", request.amount()
            )
        );
    }

    private void setTossPaymentConfirm(TossPaymentConfirmRequest request, TossPaymentConfirmResponse response) {
        when(tossPaymentProcessor.processPayment(request))
            .thenReturn(response);
    }
}
